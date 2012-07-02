package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.{Akka, Promise}
import play.api.Play.current
import play.api.libs.json.{Writes, Json, JsString}
import helpers.json._
import helpers._
import models.Sighting

object Services extends Controller with AsyncJsonService with helpers.Configuration {
	def report(latlng:String) = AsyncAction(serviceDefaultTimeoutSeconds) {
    try {
      val coords = latlng.split(",").map(_.toDouble)
      if(coords.size == 2) {
        Sighting.put(Sighting(coords(0), coords(1)))
        JsonSuccess("Sighting added")
      } else {
        JsonError("Input value not correct")
      }
    } catch {
      case e:Exception => JsonError("Input value not correct")
    }
	}

	def nearby = AsyncAction(serviceDefaultTimeoutSeconds) {
		JsonError("Not implemented yet")
	}

	def area = AsyncAction(serviceDefaultTimeoutSeconds) {
		JsonError("Not implemented yet")
	}
}