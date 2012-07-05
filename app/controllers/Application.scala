package controllers

import play.api._
import play.api.mvc._
import play.api.Logger
import helpers._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
}