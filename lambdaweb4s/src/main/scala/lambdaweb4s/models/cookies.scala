package lambdaweb4s.models

import java.net.{URLDecoder, URLEncoder}
import java.text.SimpleDateFormat
import java.time.Instant
import scala.util.Try
import java.util.{Date, TimeZone}

case class RequestCookie(name: String, content: String) {
  override def toString: String = {
    val encodedContent = URLEncoder.encode(content, "UTF-8")
    s"$name=$encodedContent"
  }
}

object RequestCookie {
  val valSep = "="
  def fromString(rawCookie: String): RequestCookie = {
    val parts = rawCookie.split(valSep)
    RequestCookie(parts(0), parts(1))
  }
}

case class ResponseCookie(
                   name: String,
                   content: String,
                   domain: Option[String] = None,
                   path: Option[String] = None,
                   expires: Option[Long] = None,
                   sameSite: Option[String] = None,
                   secure: Boolean = false,
                   httpOnly: Boolean = false
  )
{
  override def toString: String = {
    val encodedContent = URLEncoder.encode(content, "UTF-8")
    var cookie = s"$name=$encodedContent"
    if (domain.isDefined) {
      cookie = s"$cookie; domain=${domain.get}"
    }
    if (path.isDefined) {
      cookie = s"$cookie; path=${path.get}"
    }
    if (expires.isDefined) Try {
      val date = Date.from(Instant.ofEpochSecond(expires.get))
      cookie = s"$cookie; expires=${ResponseCookie.dateFormat.format(date)}"
    }
    if (sameSite.isDefined) {
      cookie = s"$cookie; SameSite=${sameSite.get}"
    }
    if (secure)
      cookie = s"$cookie; Secure"
    if (httpOnly)
      cookie = s"$cookie; HttpOnly"
    cookie
  }
}

object ResponseCookie {
  val valSep = "="
  val delimWithSpace = "; "
  val delim = ";"
  val dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'")
  dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))

  def fromString(rawCookie: String): ResponseCookie = {
    val parsed = rawCookie.replace(delimWithSpace, delim).split(delim)
      .map(x => x.split(valSep))
      .filter(_.length >= 1)
      .map(x => x(0) -> { if (x.isDefinedAt(1)) x(1) else ""} )

    val (name, content) = parsed(0)
    val data = parsed.tail.toMap

    ResponseCookie(
      name = name,
      content = URLDecoder.decode(content, "UTF-8"),
      domain = data.get("domain"),
      path = data.get("path"),
      expires = Try(data.get("expires").map(date => dateFormat.parse(date).getTime / 1000)).toOption.flatten,
      sameSite = data.get("SameSite"),
      secure = data.contains("Secure"),
      httpOnly = data.contains("HttpOnly")
    )
  }
}
