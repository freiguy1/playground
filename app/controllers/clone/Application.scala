package controllers.clone

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.clone.index())
  }

  def itsMe = Action{
    Redirect(routes.Application.index).withCookies(Cookie("itsMe", "true"))
  }
}
