package models
import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import helpers.{Record, Mapper}

case class Sighting(lat: Double, lng: Double, timestamp:DateTime, id:Option[String] = None)

object Sighting extends Record[Sighting] {
  val collection = "sighting"

  implicit object SightingMapper extends Mapper[Sighting] {
    def read(o: MongoDBObject) = for {
      lat <- o.getAs[Double]("lat")
      lng <- o.getAs[Double]("lng")
      timestamp <- o.getAs[Long]("timestamp").flatMap(instant=>Some(new DateTime(instant)))
      id <- o.getAs[String]("_id")
    } yield(Sighting(lat, lng, timestamp, Some(id)))

    def write(t:Sighting) = MongoDBObject(
      "lat" -> t.lat, "lng" -> t.lng, "timestamp" -> t.timestamp.toInstant.getMillis
    )
  }
}