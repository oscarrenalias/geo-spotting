package helpers

import play.api.libs.json._
import java.text.SimpleDateFormat
import java.util.Date
import org.specs2.internal.scalaz.Writers

package object json {
  implicit def String2JsString(x:String) = JsString(x)
  implicit def Double2JsNumber(d:Double)= JsNumber(d)
  implicit def Long2JsNumber(l:Long) = JsNumber(l)
  implicit def Int2JsNumber(i:Int) = JsNumber(i)
  implicit def Boolean2JsBoolean(x:Boolean) = JsBoolean(x)
  implicit def Date2JsString(d:Date) = {
    JsString((new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")).format(d))
  }  

  case class JsonResponse(val error:Boolean, val message:String)
  // TODO: why do I need a companion object for this? It should be possible to play the implicit object in the case class!
  object JsonResponse {
    implicit object JsonResponseWriter extends JsonResponseWriter[JsonResponse]
  }

  protected[this] class JsonResponseWriter[T <: JsonResponse] extends Writes[T] {
    def writes(x:T): JsValue = {
      JsObject(List(
        "error" -> JsBoolean(x.error),
        "message" -> JsString(x.message)
      ))
    }
  }

  case class JsonError(override val message:String) extends JsonResponse(true, message)
  object JsonError {
    implicit object JsonError extends JsonResponseWriter[JsonError]
  }

  case class JsonSuccess(override val message:String) extends JsonResponse(false, message)
  object JsonSuccess {
    implicit object JsonSuccess extends JsonResponseWriter[JsonError]
  }
}