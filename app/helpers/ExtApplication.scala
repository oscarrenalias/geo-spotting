import play.api.Application
import com.mongodb.casbah.Imports._

package object helpers {
	class ExtApplication(app:Application) extends {	
		lazy val mongo = (for {
			host <- app.configuration.getString("mongodb.host")
			port <- app.configuration.getInt("mongodb.port")
			database <- app.configuration.getString("mongodb.database")
		} yield(MongoConnection(host, port)(database))).getOrElse(throw(app.configuration.globalError("Fatal error: cannot connect to data store")), None)
	}

	implicit def Application2ExtApplication(a:Application) = new ExtApplication(a)
}