import helpers.Store
import play.api.{Logger, Application, GlobalSettings}

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Starting application")
  }
}
