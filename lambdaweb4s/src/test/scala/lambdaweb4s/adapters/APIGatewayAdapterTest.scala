package lambdaweb4s.adapters

class APIGatewayAdapterTest extends munit.FunSuite  {
  test("parse API gateway REST request") {
    val input = """
      |{
      |"resource": "/",
      |"path": "/foo",
      |"httpMethod": "GET",
      |"requestContext": {
      |    "resourcePath": "/",
      |    "httpMethod": "GET",
      |    "path": "/Prod/"
      |},
      |"headers": {
      |    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
      |    "accept-encoding": "gzip, deflate, br"
      |},
      |"multiValueHeaders": {
      |    "accept": [
      |        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
      |    ],
      |    "accept-encoding": [
      |        "gzip, deflate, br"
      |    ]
      |},
      |"queryStringParameters": null,
      |"multiValueQueryStringParameters": null,
      |"pathParameters": null,
      |"stageVariables": null,
      |"body": null,
      |"isBase64Encoded": false
      |}
      |""".stripMargin

    val adapter = new APIGatewayAdapter(input)
    val actual = adapter.parse
    val expected = Right(
      APIGatewayRequest(
        "/foo",
        "GET",
        None,
        Option(Map(
          "accept" -> List("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"),
          "accept-encoding" -> List("gzip, deflate, br")
        )),
        None,
        false)
    )
    assertEquals(actual, expected)
  }

  test("parse API gateway REST request with no headers") {
    val input = """
                  |{
                  |"resource": "/",
                  |"path": "/",
                  |"httpMethod": "GET",
                  |"requestContext": {
                  |    "resourcePath": "/",
                  |    "httpMethod": "GET",
                  |    "path": "/Prod/"
                  |},
                  |"queryStringParameters": null,
                  |"multiValueQueryStringParameters": null,
                  |"pathParameters": null,
                  |"stageVariables": null,
                  |"body": null,
                  |"isBase64Encoded": false
                  |}
                  |""".stripMargin

    val adapter = new APIGatewayAdapter(input)
    val actual = adapter.parse
    val expected = Right(APIGatewayRequest("/", "GET", None, None, None, false))
    assertEquals(actual, expected)
  }
}
