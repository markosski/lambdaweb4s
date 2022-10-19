package lambdaweb4s.util

import lambdaweb4s.models.{Headers, HttpCodes, Response}
import org.slf4j.LoggerFactory

import java.io.ByteArrayOutputStream
import scala.util.{Failure, Success, Try}

object staticContent {
  val logger = LoggerFactory.getLogger(this.getClass)

  def contentFromResources(filePath: String, contentType: String): Response = {
    Try {
      val classLoader = getClass.getClassLoader
      val is = classLoader.getResourceAsStream(filePath)
      val bytes: Array[Byte] = Array.ofDim[Byte](4096) // 4KB
      val os = new ByteArrayOutputStream()

      var byteRead = 0;
      while (byteRead != -1) {
        os.write(bytes, 0, byteRead)
        byteRead = is.read(bytes, 0, bytes.length)
      }

      is.close()
      os.close()
      os.toByteArray
    } match {
      case Success(bytes) => {
        Response(HttpCodes.OK, bytes, Map(Headers.CONTENT_TYPE -> List(contentType)))
      }
      case Failure(err) => {
        logger.error(err.getMessage)
        Response(HttpCodes.NOT_FOUND, Array())
      }
    }
  }
}
