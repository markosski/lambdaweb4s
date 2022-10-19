package lambdaweb4s.models

object Methods extends Enumeration {
  type Method = Value
  val GET, POST, PUT, PATCH, DELETE = Value
}

object HttpCodes {
  val OK = 200
  val CREATED = 201
  val ACCEPTED = 202
  val PERMANENT_REDIRECT = 301
  val TEMPORARY_REDIRECT = 302
  val NOT_MODIFIED = 304
  val BAD_REQUEST = 400
  val UNAUTHORIZED = 401
  val FORBIDDEN = 403
  val NOT_FOUND = 404
  val INTERNAL_ERROR = 500
  val SERVICE_UNAVAILABLE = 503
}

object Headers {
  val CONTENT_TYPE = "content-type"
  val COOKIE = "cookie"
  val SET_COOKIE = "set-cookie"
  val Location = "location"
  val SERVER = "server"
}

object ContentType {
  val TEXT_JAVASCRIPT = "text/javascript"
  val TEXT_HTML = "text/html"
  val TEXT_CSS = "text/css"
  val TEXT_PLAIN = "text/plain"
  val APPLICATION_JSON = "application/json"
  val IMAGE_PNG = "image/png"
  val IMAGE_JPEG = "image/jpeg"
}
