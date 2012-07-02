import play.api.Application
import com.mongodb.casbah.Imports._
import helpers._

package object helpers {
	class ExtApplication(app:Application) {	
		lazy val mongo = Store.mongo
	}

	implicit def Application2ExtApplication(a:Application) = new ExtApplication(a)
}