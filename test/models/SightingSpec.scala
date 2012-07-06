package models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import com.mongodb.casbah.Imports._

class SightingSpec extends Specification {
  "The sighting model" should {
    "insert data correctly" in {
      running(FakeApplication()) {
        val toInsert = Sighting(41.1, 1.5)
        val sighting = Sighting.put(toInsert)
        sighting must beLeft
      }
      "retrieve data correctly using a query" in {
        import Sighting._
        Sighting.put(Sighting(10.0,0.5))
        Sighting.put(Sighting(12.0,1.5))
        Sighting.query(("lat" $gte 10 $lte 12.0) ++ ("lng" $gte 0.5 $lte 1.5)) must not be empty
      }
    }
  }
}