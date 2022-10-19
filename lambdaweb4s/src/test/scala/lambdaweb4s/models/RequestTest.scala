package lambdaweb4s.models

class RequestTest extends munit.FunSuite {
  test("Get cookies in single header") {
    val request = Request(Path("" :: Nil), Methods.GET, Map(), Map("cookie" -> List("foo=bar; fiz=buz;fix=fux")), Some(""))
    val expected = RequestCookie("foo", "bar") :: RequestCookie("fiz", "buz") :: RequestCookie("fix", "fux") :: Nil
    assertEquals(request.cookies.toList, expected)
  }

  test("Get cookies in multi header") {
    val request = Request(Path("" :: Nil), Methods.GET, Map(), Map("cookie" -> List("foo=bar","fiz=buz")), Some(""))
    val expected = RequestCookie("foo", "bar") :: RequestCookie("fiz", "buz") :: Nil
    assertEquals(request.cookies.toList, expected)
  }
}
