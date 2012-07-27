package helpers

import com.mongodb.casbah.Imports._
import play.api.Logger
import com.mongodb.casbah.{MongoDB, MongoURI}

object Store {
	import play.api.Play.current

  def storeError(e:Option[Throwable] = None) = current.configuration.globalError("Error connecting to MongoDB. Make sure mongodb.url is correctly defined in application.properties.", e)

  lazy val mongo = {
    val mongoURI = MongoURI(
      scala.util.Properties.envOrElse("MONGOHQ_URL",
        current.configuration.getString("mongodb.url").getOrElse({throw storeError();""})
    ))
    mongoURI.connectDB.fold(
      error => { throw storeError(Some(error)); /* dummy mongo DB */ MongoDB(MongoConnection(""), "") },
      db => { db.authenticate(mongoURI.username.getOrElse(""), mongoURI.password.getOrElse("").toString); db }
    )
  }

  def withStore[T](f: => MongoDB => T) = { f(mongo) }
}

trait Record[T] {
  import helpers.Store._

  // mongo collection to which  this entity belongs - must be provided by classes mixing in this trait
  val collection: String

  type RecordException = Exception

  // insert a record to the database
  def put(t: T)(implicit mapper:Mapper[T]):Either[T,RecordException] = withStore { conn =>
    conn(collection).insert(mapper.write(t)) match {
      case x if x.getError == null => Left(t)
      case x => Right(new Exception(x.getError))
    }
  }

  def get[T](id:String)(implicit mapper:Mapper[T]): Option[T] = withStore { conn =>
    val q = MongoDBObject( "_id" -> id)
    conn(collection).findOne(q).flatMap(obj => mapper.read(obj))
  }

  def query[T](query:DBObject)(implicit mapper:Mapper[T]): Seq[T] = withStore { conn =>
    var result = scala.collection.mutable.Seq[T]()
    for(obj <- conn(collection).find(query)) {
      mapper.read(obj) match {
        case Some(x) => result = result :+ x; x   // TODO: not very functional
        case _ => {}
      }
    }

    result
  }
}

trait Mapper[T] {
  def read(o: MongoDBObject): Option[T]
  def write(t: T): MongoDBObject
}