package lambdaweb4s

import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import lambdaweb4s.adapters.HttpExchangeAdapter
import lambdaweb4s.models.{Headers, HttpCodes}
import org.slf4j.LoggerFactory

class DevWebServer(handler: LambdaWebHandler) {
  val logger = LoggerFactory.getLogger(this.getClass)

  val banner: String =
    """
      | ___      _______  __   __  _______  ______   _______    _     _  _______  _______  _   ___  _______
      ||   |    |   _   ||  |_|  ||  _    ||      | |   _   |  | | _ | ||       ||  _    || | |   ||       |
      ||   |    |  |_|  ||       || |_|   ||  _    ||  |_|  |  | || || ||    ___|| |_|   || |_|   ||  _____|
      ||   |    |       ||       ||       || | |   ||       |  |       ||   |___ |       ||       || |_____
      ||   |___ |       ||       ||  _   | | |_|   ||       |  |       ||    ___||  _   | |___    ||_____  |
      ||       ||   _   || ||_|| || |_|   ||       ||   _   |  |   _   ||   |___ | |_|   |    |   | _____| |
      ||_______||__| |__||_|   |_||_______||______| |__| |__|  |__| |__||_______||_______|    |___||_______|
      |""".stripMargin

	def listen(port: Int): Unit = {
    val server = HttpServer.create(new InetSocketAddress(port), 0);
    println("Welcome to the LambdaWeb4s Development Web Server")
    println(banner)
    println(s"Listening on port: $port")
    println(s"Ctrl-C to stop server")
    server.createContext("/", new MyHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  def handleWebServerRequest(sourceRequest: HttpExchange): Unit = {
    val SERVER_ERROR = "SERVER_ERROR"
    logger.info(sourceRequest.getRequestURI.toString)

    val adapter = new HttpExchangeAdapter(sourceRequest)
    val resp = for {
      request <- adapter.mapRequest
      response0 = handler.route(request)
      response = response0.addHeader(Headers.SERVER, List("lambdaweb4s development server"))
      mapped <- adapter.mapResponse(response)
    } yield mapped

    resp match {
      case Right(_) => ()
      case Left(err) => {
        logger.error(err.getStackTrace.mkString("\n"))
        sourceRequest.sendResponseHeaders(HttpCodes.INTERNAL_ERROR, SERVER_ERROR.length)
        val os = sourceRequest.getResponseBody
        os.write(SERVER_ERROR.getBytes)
        os.close()
      }
    }
  }

  class MyHandler extends HttpHandler {
    override def handle(request: HttpExchange): Unit = {
      handleWebServerRequest(request)
    }
  }
}
