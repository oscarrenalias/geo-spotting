package helpers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{Json, Writes}
import play.api.libs.concurrent.Akka
import play.api.Play.current
import helpers.json._

trait AsyncJsonService extends Controller {
  def WithFuture[T](timeoutSeconds:Int)(f: => T)(implicit jsonHelper:Writes[T]) = {
    Akka.future {
      f
    } orTimeout(Ok(Json.toJson(JsonError("Timeout while reading data"))), timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS) map { result =>
      result.fold(
        data => Ok(Json.toJson(data)),
        error => Ok(Json.toJson(JsonError("Error while waiting for future:" + error)))
      )
    }
  }

  def AsyncAction[T](timeoutSeconds:Int)(f: => T)(implicit jsonHelper:Writes[T]) = Action {
    Async {
      WithFuture(timeoutSeconds) {
        f
      }
    }
  }
}
