package lambdaweb4s.adapters

class AlbAdapterTest extends munit.FunSuite {
  test("parse ALB request 1") {
    val input = """
                  |{
                  |"requestContext": {
                  |"elb": {
                  |"targetGroupArn": "arn:aws:elasticloadbalancing:us-east-1:685250009713:targetgroup/dev-cerb-dynamo-decoder-tg/721e909fa8d619a1"
                  |}
                  |},
                  |"httpMethod": "GET",
                  |"path": "/",
                  |"multiValueQueryStringParameters": {"bar": ["c"],"foo": ["a","b"]
                  |},
                  |"multiValueHeaders": {
                  |"accept": ["text/html,application/xhtml+xml,application/xml;q\u003d0.9,image/avif,image/webp,image/apng,*/*;q\u003d0.8,application/signed-exchange;v\u003db3;q\u003d0.9"],
                  |"accept-encoding": ["gzip, deflate, br"],
                  |"cache-control": ["max-age\u003d0"],
                  |"cookie": ["foo=bar"],
                  |"user-agent": ["Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36"]
                  |},
                  |"body": "",
                  |"isBase64Encoded": false
                  |}
                  |""".stripMargin

    val adapter = new ALBAdapter(input)
    val actual = adapter.parse
    val expected = Right(
      ALBRequest(
        "/",
        "GET",
        Some(""),
        Some(Map(
          "accept" -> List("text/html,application/xhtml+xml,application/xml;q\u003d0.9,image/avif,image/webp,image/apng,*/*;q\u003d0.8,application/signed-exchange;v\u003db3;q\u003d0.9"),
          "accept-encoding" -> List("gzip, deflate, br"),
          "cookie" -> List("foo=bar"),
          "cache-control" -> List("max-age\u003d0"),
          "user-agent" -> List("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.64 Safari/537.36")
        )),
        Some(Map(
          "bar" -> List("c"),
          "foo" -> List("a", "b")
        )),
        false
      )
    )
    assertEquals(actual, expected)
  }

  test("parse ALB request 2") {
    val input = """
                  |{
                  |"httpMethod": "GET",
                  |"path": "/",
                  |"multiValueQueryStringParameters": {"bar": ["c"],"foo": ["a","b"]},
                  |"multiValueHeaders": {
                  |"accept-encoding": ["gzip, deflate, br"],
                  |"cookie": ["foo=bar"]
                  |},
                  |"body": "Y29udGVudD1mb28lMjBiYXI=",
                  |"isBase64Encoded": true
                  |}
                  |""".stripMargin

    val adapter = new ALBAdapter(input)
    val actual = adapter.parse
    val expected = Right(
      ALBRequest(
        "/",
        "GET",
        Some("Y29udGVudD1mb28lMjBiYXI="),
        Some(Map(
          "accept-encoding" -> List("gzip, deflate, br"),
          "cookie" -> List("foo=bar")
        )),
        Some(Map(
          "bar" -> List("c"),
          "foo" -> List("a", "b")
        )),
        true
      )
    )
    assertEquals(actual, expected)
  }

  test("get decoded base64 data from request") {
    val input = """
                  |{
                  |"httpMethod": "GET",
                  |"path": "/",
                  |"multiValueQueryStringParameters": {"bar": ["c"],"foo": ["a","b"]},
                  |"multiValueHeaders": {
                  |"accept-encoding": ["gzip, deflate, br"],
                  |"cookie": ["foo=bar"]
                  |},
                  |"body": "Y29udGVudD1mb28lMjBiYXI=",
                  |"isBase64Encoded": true
                  |}
                  |""".stripMargin

    val adapter = new ALBAdapter(input)
    adapter.mapRequest match {
      case Right(r) => assertEquals(r.data, Map("content" -> List("foo bar")))
      case Left(err) => throw err
    }
  }
}