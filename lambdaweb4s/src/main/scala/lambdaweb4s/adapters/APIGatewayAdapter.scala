package lambdaweb4s.adapters

import lambdaweb4s.models.{Methods, Path, Request, Response}
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.{DefaultScalaModule, ScalaObjectMapper}

import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.Base64
import scala.util.{Failure, Success, Try}

case class APIGatewayRequest(
                       path: String,
                       httpMethod: String,
                       body: Option[String],
                       multiValueHeaders: Option[Map[String, List[String]]],
                       multiValueQueryStringParameters: Option[Map[String, List[String]]],
                       isBase64Encoded: Boolean
                     )

case class APIGatewayResponse(
                        statusCode: Int,
                        body: String,
                        multiValueHeaders: Map[String, List[String]],
                        isBase64Encoded: Boolean
                      )


class APIGatewayAdapter(rawReq: String) extends RequestResponseAdapter[Array[Byte]]{
  private val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def parse: Either[Throwable, APIGatewayRequest] = {
    Try {
      mapper.readValue[APIGatewayRequest](rawReq)
    } match {
      case Success(x) => Right(x)
      case Failure(err) => Left(new Exception(err.getMessage + " while JSON to Object conversion: " + rawReq))
    }
  }

  def toJson(response: APIGatewayResponse): Either[Throwable, Array[Byte]] = {
    Try {
      mapper.writeValueAsString(response)
    } match {
      case Success(x) => Right(x.getBytes())
      case Failure (err) => Left(new Exception(err.getMessage + " while converting Object to JSON"))
    }
  }

  def mapRequest: Either[Throwable, Request] = {
    for {
      parsed <- parse
      path = parsed.path
      method <- parseMethod(parsed.httpMethod)
      params = parsed.multiValueQueryStringParameters.getOrElse(Map())
      headers = parsed.multiValueHeaders.getOrElse(Map())
      body =
        if (parsed.isBase64Encoded)
          parsed.body.map(
            b => URLDecoder.decode(
              new String(Base64.getDecoder.decode(b), Charset.forName("UTF-8")),
              "UTF-8"
            )
          )
        else parsed.body
    } yield Request(Path.from(path), method, params, headers, body, Some(rawReq))
  }

  def mapResponse(resp: Response): Either[Throwable, Array[Byte]] = Try {
    APIGatewayResponse(
      statusCode = resp.code,
      body = Base64.getEncoder.encodeToString(resp.body),
      multiValueHeaders = resp.headers,
      isBase64Encoded = true
    )
  }.toEither.flatMap(toJson)

  def parseMethod(method: String): Either[Throwable, Methods.Method] = {
    Try {
      Methods.withName(method.toUpperCase)
    }.toEither.fold(err => Left(err), Right(_))
  }
}
