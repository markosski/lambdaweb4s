# lambdaweb4s

**lambdaweb4s** is a lightweight library that bridges multiple AWS web integrations into single 
response/request model. The benefits of this approach are the following:

* convenient pattern matching syntax to map request to response objects
* zero dependency built-in development web server to test lambda locally
* lambda can be deployed in front of ALB or API Gateway without any change to source code

## Usage

Add `lambdaweb4s` dependency to your build system and create class that extends `LambdaWebHandler`


```scala
import lambdaweb4s.models.Methods._
import lambdaweb4s.LambdaWebHandler
import lambdaweb4s.DevWebServer
import lambdaweb4s.models._
import lambdaweb4s.models.Path.Root
import lambdaweb4s.models.Codes._

class Handler extends LambdaWebHandler {
  def accountInfo(accountId: String): Response = {
    Response.ok(s"Info for account: $accountId")     
  }

  def routes = {
    case GET -> Root => Response.ok("Hello World")
    case GET -> Root / "_health" => Response.ok("healthy")
    case GET -> Root / "accounts" / accountId / "info" => accountInfo(accountId)
    case _ => Response.text(NOT_FOUND, "page not found")
  }
}

object Handler extends App {
  val server = new DevWebServer(new Handler)
  server.listen(8080)
}
```

## Run Locally

After building uber jar with build plugin of your choice you can run lambda app locally

`java -jar application.jar`

