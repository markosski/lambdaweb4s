package example

import lambdaweb4s.models.Request
import lambdaweb4s.models.Response
import lambdaweb4s.models.Methods._
import lambdaweb4s.models.Path.Root
import lambdaweb4s.models._
import lambdaweb4s.LambdaWebHandler
import lambdaweb4s.DevWebServer
import lambdaweb4s.util.staticContent

import scala.util._

case class UserId(userId: Int)
object UserId {
	def apply(rawUserId: String): Option[UserId] = Try(UserId(Integer.valueOf(rawUserId))).toOption
}

class Handler extends LambdaWebHandler {
	def welcome(request: Request): Response = {
		Response.ok(s"welcome to lambdaweb4s, request data: $request")
	}

	def users(request: Request): Response = {
		Response.ok(s"list all users, request data: $request")
	}

	def createUser(request: Request): Response = {
		Response.ok(s"list all users, post data: ${request.data}")
	}

	def user(userId: UserId, request: Request): Response = {
		Response.ok(s"user info for $userId, request data: $request")
	}

	def accountInfoRoute(accountId: String, request: Request): Response = {
		Response.ok(s"account info for $accountId, request data: $request")
	}

	def routes: PartialFunction[Request, Response] = {
		case GET 				-> Root / "_health" => Response.ok("healthy")
		case GET 				-> Root / "static" / "logo.png" => staticContent.contentFromResources("logo.png", ContentType.IMAGE_PNG)
		case GET 				-> Path("static" :: path) => staticContent.contentFromResources(path.mkString("/"), ContentType.TEXT_PLAIN)
		case req @ GET 	-> Root => welcome(req)
		case req @ GET 	-> Root / "users" => users(req)
		case req @ POST -> Root / "users" => createUser(req)
		case req @ GET 	-> Root / "users" / userId if UserId(userId).isDefined => user(UserId(userId).get, req)
		case GET 				-> Path("users" :: path) => Response.ok(s"remaining path: $path")
		case req @ GET 	-> Root / "account" / accountId / "info" => accountInfoRoute(accountId, req)
	}
}

object Handler extends App {
	val server = new DevWebServer(new Handler)
	server.listen(8080)
}