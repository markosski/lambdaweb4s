package lambdaweb4s.adapters

import lambdaweb4s.models.Request
import lambdaweb4s.models.Response

trait RequestResponseAdapter[R] {
	def mapRequest: Either[Throwable, Request]
	def mapResponse(resp: Response): Either[Throwable, R]
}
