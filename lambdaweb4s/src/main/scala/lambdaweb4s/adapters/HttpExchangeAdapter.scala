package lambdaweb4s.adapters

import lambdaweb4s.models.{Methods, Path, Request, Response}
import com.sun.net.httpserver.HttpExchange

import java.nio.charset.Charset
import scala.jdk.CollectionConverters._
import scala.util.Try

class HttpExchangeAdapter(req: HttpExchange) extends RequestResponseAdapter[Unit] {
	def mapRequest: Either[Throwable, Request] = {
		Try(Request(
			Path.from(req.getRequestURI.getPath),
			getMethod(req),
			getParams(req),
			getHeaders(req),
			getBody(req)
			)).toEither
	}

	def mapResponse(resp: Response): Either[Throwable, Unit] = Try {
		val contentLength = if (resp.body.isEmpty) -1 else resp.body.length
		resp.headers.foreach(h => req.getResponseHeaders.set(h._1, h._2.mkString("; ")))
		req.sendResponseHeaders(resp.code, contentLength)

		if (resp.body.nonEmpty) {
			val os = req.getResponseBody();
			os.write(resp.body);
			os.close();
		}
	}.toEither

	def getHeaders(req: HttpExchange): Map[String, List[String]] = {
		req.getRequestHeaders.asScala.toMap
			.map{ case (k, v) => (k, v.asScala.toList) }
	}
	def getParams(req: HttpExchange): Map[String, List[String]] = {
		val params: List[String] =
			Option(req.getRequestURI.getQuery) match {
				case Some(x) => x.split("&").toList
				case None => List()
			}

		if (params.nonEmpty) {
			params.map(x => {
				val parts = x.split("=")
				if (parts.size == 2)
					parts(0) -> parts(1)
				else
					parts(0) -> ""
			}).groupMap(_._1)(_._2)
		} else {
			Map()
		}
	}
	def getMethod(req: HttpExchange): Methods.Method = {
		Methods.withName(req.getRequestMethod.toUpperCase)
	}

	def getBody(req: HttpExchange): Option[String] = {
		val is = req.getRequestBody
		if (is.available() > 0) {
			val bytes: Array[Byte] = Array.fill(is.available())(0)
			is.read(bytes)
			is.close()
			Some(new String(bytes, Charset.forName("UTF-8")))
		} else {
			is.close()
			None
		}
	}
}
