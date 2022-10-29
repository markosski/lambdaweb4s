package lambdaweb4s

import com.amazonaws.services.lambda.runtime.{Context, RequestStreamHandler}
import lambdaweb4s.adapters.{ALBAdapter, APIGatewayAdapter}
import lambdaweb4s.models.{HttpCodes, Request, Response}
import lambdaweb4s.models.HttpCodes._
import org.slf4j.LoggerFactory

import java.io.{BufferedReader, InputStream, InputStreamReader, OutputStream}
import java.nio.charset.Charset
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

trait LambdaWebHandler extends RequestStreamHandler {
  protected val logger = LoggerFactory.getLogger(this.getClass)
  def routes: PartialFunction[Request, Response]

  private[this] def defaultRoutes: PartialFunction[Request, Response] = {
    case _ => Response.text(NOT_FOUND, "page not found")
  }

  private[this] def getAllRoutes = routes orElse defaultRoutes

  def route(request: Request): Response = {
    Try(getAllRoutes.apply(request)) match {
      case Success(value) => value
      case Failure(exception) => {
        logger.error(s"Error when executing route for request: $request", exception)
        Response(HttpCodes.INTERNAL_ERROR, Array(), Map())
      }
    }
  }

  def handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context): Unit = {
    val reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")))
    val event = reader.lines().iterator().asScala.mkString(System.lineSeparator())

    val adapterRequestPair = List(
      new ALBAdapter(event),
      new APIGatewayAdapter(event)
    ).map(a => (a, a.mapRequest))

    if (adapterRequestPair.filter(_._2.isRight).isEmpty) {
      adapterRequestPair.filter(_._2.isLeft).foreach(
        pair => pair._2 match {
          case Left(err) => logger.error(s"Could not map adapter: ${pair._1.getClass}; error: ${err.getMessage}")
          case Right(_) => ()
        })
    }

    adapterRequestPair.collectFirst { case (a, Right(r)) => (a, r) } match {
      case Some(pair) => {
        val adapter = pair._1
        val response = route(pair._2)

        logger.info(s"Adapter: ${adapter.getClass}")
        adapter.mapResponse(response) match {
          case Left(err) => {
            logger.error(err.getMessage)
            logger.error(err.getStackTrace.map(x => x.toString).mkString)
          }
          case Right(bytes) => outputStream.write(bytes)
        }
      }
      case None => logger.error("Could not handle request by any adapters provided")
    }
    outputStream.close()
    inputStream.close()
  }
}
