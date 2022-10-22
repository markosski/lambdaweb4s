package lambdaweb4s.models

case class Response(
                     code: Int,
                     body: Array[Byte],
                     headers: Map[String, List[String]] = Map()
                   ) {
  def cookies(cookies: Seq[ResponseCookie]): Response = {
      this.copy(headers = headers + (Headers.SET_COOKIE -> cookies.map(_.toString).toList))
  }

  def removeHeader(name: String): Response = {
    this.copy(headers = headers - name)
  }

  def addHeader(name: String, value: List[String]): Response = {
    this.copy(headers = headers + (name -> value))
  }

  def code(code: Int): Response = this.copy(code = code)

  def body(body: Array[Byte]): Response = this.copy(body = body)
}

object Response {
  def text(code: Int, htmlOrText: String): Response = {
    Response(code, htmlOrText.getBytes(), Map(Headers.CONTENT_TYPE -> List(ContentType.TEXT_HTML)))
  }

  def json(code: Int, htmlOrText: String): Response = {
    Response(code, htmlOrText.getBytes(), Map(Headers.CONTENT_TYPE -> List(ContentType.APPLICATION_JSON)))
  }

  def ok(text: String): Response = {
    Response.text(HttpCodes.OK, text)
  }

  def redirect(to: String): Response = {
    Response(
      HttpCodes.TEMPORARY_REDIRECT,
      Array(),
      Map(
        Headers.Location -> List(to)
      ))
  }
}