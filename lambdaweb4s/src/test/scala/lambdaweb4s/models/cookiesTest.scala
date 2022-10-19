package lambdaweb4s.models

class cookiesTest extends munit.FunSuite {
  test("Cookie to string conversion") {
    val cookie = ResponseCookie("mycookie", "Some content", Some("example.com"), Some("/"), Some(1651807480))

    assertEquals(s"$cookie", "mycookie=Some+content; domain=example.com; path=/; expires=Fri, 6 May 2022 03:24:40 GMT")
  }

  test("from string to Cookie") {
    val cookieString = "mycookie=Some+content; domain=example.com; path=/; expires=Fri, 6 May 2022 03:24:40 GMT"

    assertEquals(ResponseCookie.fromString(cookieString), ResponseCookie("mycookie", "Some content", Some("example.com"), Some("/"), Some(1651807480)))
  }
}
