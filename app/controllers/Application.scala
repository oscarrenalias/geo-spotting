package controllers

import play.api._
import play.api.mvc._
import play.api.Logger
import helpers._

object Application extends Controller {
  
  def index = Action {
  	import play.api.Play.current

  	Logger.debug(current.mongo.toString)

    Ok(views.html.index("Your new application is ready."))
  }
  
}