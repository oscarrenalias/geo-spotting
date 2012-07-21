package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.{Akka, Promise}
import play.api.Play.current
import libs.json._
import helpers.json._
import helpers._
import models.Sighting
import com.mongodb.casbah.Imports._
import org.joda.time.DateTime

object Services extends Controller with AsyncJsonService with helpers.Configuration {
	def report(latlng:String) = AsyncAction(serviceDefaultTimeoutSeconds) {

    def coords = latlng.split(",").map(_.toDouble) match {
      case x if x.size == 2 => Some(x(0), x(1))
      case _ => None
    }

    coords.map(c => Sighting.put(Sighting(c._1, c._2)).fold(
      success => JsonSuccess("Sighting added"),
      failure => JsonError("There was an error adding the sighting: " + failure.toString)
    )).getOrElse(JsonError("Input value not correct"))
	}

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
          Logger.trace("Query: nw=" + lat1 + "," + lng1 + " se=" + lat2 + "," + lng2)
          val q = ("lat" $gte lat2 $lte lat1) ++ ("lng" $gte lng2 $lte lng1) ++
                  ("timestamp" $gt (new DateTime).minusMonths(2).toInstant.getMillis)

          JsObject(List("error" -> false, "data" -> JsArray(Sighting.query(q).map(Json.toJson(_)))))
        }).getOrElse(JsObject(List("error" -> false, "data" -> JsArray(List()))))
      }
    }
  }
}