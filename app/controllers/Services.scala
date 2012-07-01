package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.{Akka, Promise}
import play.api.Play.current
import play.api.libs.json.{Writes, Json, JsString}
import helpers.json._

trait AsyncJsonService extends Controller {
	def WithFuture[T](timeoutSeconds:Int)(f: => T)(implicit jsonHelper:Writes[T]) = {
	    Akka.future {
	      f
	    } orTimeout(Ok(Json.toJson(JsonError("Timeout while reading data"))), timeoutSeconds, java.util.concurrent.TimeUnit.SECONDS) map { result =>
	      result.fold(
	        data => Ok(Json.toJson(data)),
	        error => Ok(Json.toJson(JsonError("Error")))
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

trait Configuration {
	lazy val serviceDefaultTimeoutSeconds = 2
}

object Services extends Controller with AsyncJsonService with Configuration {
	def report = AsyncAction(serviceDefaultTimeoutSeconds) {
		JsonError("Not implemented yet")
	}

	def nearby = AsyncAction(serviceDefaultTimeoutSeconds) {
		JsonError("Not implemented yet")
	}

	def area = AsyncAction(serviceDefaultTimeoutSeconds) {
		JsonError("Not implemented yet")
	}
}