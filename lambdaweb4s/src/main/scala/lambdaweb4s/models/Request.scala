package lambdaweb4s.models

case class Request(
                    path: Path,
                    method: Methods.Method,
                    params: Map[String, List[String]] = Map(),
                    headers: Map[String, List[String]] = Map(),
                    body: Option[String] = None,
                    rawRequest: Option[String] = None
                  ) {
  private val cookieDelimWithSpace = "; "
  private val cookieDelim = ";"

  /**
   * Parse headers for browser cookies
   * @return
   */
  def cookies: Seq[RequestCookie] = {
    headers.map{ case (k, v) => k.toLowerCase -> v}
    headers.get(Headers.COOKIE)
      .flatMap(xs => Option(xs.flatMap {
        x => {
          if (x.contains(cookieDelim)) {
            // handle concat cookies
            x.replace(cookieDelimWithSpace, cookieDelim).split(cookieDelim).map(RequestCookie.fromString).toList
          } else {
            // handle multi header cookies
            RequestCookie.fromString(x) :: Nil
          }
        }
      }))
      .getOrElse(Nil)
  }

  /**
   * Parse body of request for posted data
   * @return
   */
  def data: Map[String, List[String]] = {
    val params: List[String] = this.body.getOrElse("").split("&").toList
    if (params.nonEmpty) {
      params.map(x => {
        val parts = x.split("=")
        parts(0) -> parts(1)
      }).groupMap(_._1)(_._2)
    } else {
      Map()
    }
  }
}

object -> {
  def unapply(request: Request): Option[(Methods.Method, Path)] = {
    Some((request.method, request.path))
  }
}