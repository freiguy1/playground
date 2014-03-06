package controllers.clone.api

import play.api.libs.json._
import play.api.mvc._

import controllers.clone.JsonConverters._

import models.clone._

object BaseApi extends Controller {

  def time() = Action { request =>
    Ok(Json.toJson(Map("time" -> new java.util.Date())))
  }

}
