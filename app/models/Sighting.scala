package models
import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import helpers.{Record, Mapper}
import play.api.libs.json._
import helpers.json._

case class Sighting(lat: Double, lng: Double, timestamp:DateTime = new DateTime, id:Option[String] = None)

object Sighting extends Record[Sighting] {
  val collection = "sighting"

  implicit object SightingMapper extends Mapper[Sighting] {
    def read(o: MongoDBObject) = for {
      lat <- o.getAs[Double]("lat")
      lng <- o.getAs[Double]("lng")
      timestamp <- o.getAs[Long]("timestamp").flatMap(instant=>Some(new DateTime(instant)))
      id <- o.get("_id")
    } yield(Sighting(lat, lng, timestamp, Some(id.toString)))

    def write(t:Sighting) = t.id match {
      case None => MongoDBObject("lat" -> t.lat, "lng" -> t.lng, "timestamp" -> t.timestamp.toInstant.getMillis)
      case Some(id) => MongoDBObject("lat" -> t.lat, "lng" -> t.lng, "timestamp" -> t.timestamp.toInstant.getMillis, "_id" -> id)
    }
  }

  implicit object SightingWriter extends Writes[Sighting] {
    def writes(x:Sighting) = JsObject(List(
      "id" -> x.id.getOrElse("").toString,
      "lat" -> x.lat,
      "lng" -> x.lng,
      "timestamp" -> JsObject(List(
        "unix" -> x.timestamp.toInstant.getMillis,
        "date" -> x.timestamp.toString
      ))
    ))
  }
}