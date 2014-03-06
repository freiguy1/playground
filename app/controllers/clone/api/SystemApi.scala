package controllers.clone.api

import play.api.libs.json._
import play.api.mvc._

import controllers.clone.JsonConverters._

import models.clone._

object SystemApi extends Controller {

  def get() = Action { request =>
    Ok(Json.toJson(System.get))
  }

}
