package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.{Akka, Promise}
import play.api.Play.current
import play.api.libs.json.{Writes, Json, JsString}
import helpers.json._
import helpers._
import models.Sighting
import com.mongodb.casbah.Imports._
import org.joda.time.DateTime

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

	/*def area = AsyncAction(serviceDefaultTimeoutSeconds) {
		JsonError("Not implemented yet")
	}*/

  def area =  Action { request =>
    Async {
      WithFuture(serviceDefaultTimeoutSeconds) {
        // flatten the query string, as otherwise it comes as Map(param->Buffer(value))
        val params = request.queryString.flatMap(k => Map(k._1 -> k._2(0).toDouble))
        (for {
          lat1 <- params.get("lat1")
          lng1 <- params.get("lng1")
          lat2 <- params.get("lat2")
          lng2 <- params.get("lng2")
        } yield {
          import models.Sighting._
          var twoMonthsAgo = (new DateTime).minusMonths(2)
          Logger.debug("Query: nw=" + lat1 + "," + lng1 + " se=" + lat2 + "," + lng2)
          val q = ("lat" $gte lat2 $lte lat1) ++ ("lng" $gte lng2 $lte lng1) ++
                  ("timestamp" $gt twoMonthsAgo.toInstant.getMillis)
        }).getOrElse(JsonError("Incorrect parameters"))
      }
    }
  }
}