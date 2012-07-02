  package helpers

import com.mongodb.casbah.Imports._
import helpers.Store._

object Store {
	import play.api.Play.current

	lazy val mongo = (for {
		host <- current.configuration.getString("mongodb.host")
		port <- current.configuration.getInt("mongodb.port")
		database <- current.configuration.getString("mongodb.database")
	} yield(MongoConnection(host, port))(database)).getOrElse(throw(current.configuration.globalError("Fatal error: cannot connect to data store")))

	def withStore[T](f: => MongoDB => T) = { f(mongo) }
}

trait Record[T] {
  // mongo collection to which  this entity belongs - must be provided by classes mixing in this trait
  val collection: String

  // load a record from the database
  def put(t: T)(implicit mapper:Mapper[T]) = withStore { conn =>
    conn(collection).insert(mapper.write(t))
  }

  def get[T](id:String)(implicit mapper:Mapper[T]): Option[T] = withStore { conn =>
    val q = MongoDBObject( "_id" -> id)
    conn(collection).findOne(q).flatMap(obj => mapper.read(obj))
  }
}

trait Mapper[T] {
  def read(o: MongoDBObject): Option[T]
  def write(t: T): MongoDBObject
}