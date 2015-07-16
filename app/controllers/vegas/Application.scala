package controllers.vegas

import play.api._
import play.api.mvc._

import controllers.vegas.JsonConverters._
import models.vegas._
import play.api.libs.json._

object Application extends Controller with securesocial.core.SecureSocial {

  def index = Action { implicit request =>
    Ok(views.html.vegas.index(Accessor.getTeams))
  }

  def addTeam = Action(parse.json) { implicit request =>
    request.body.validate[NewTeam].map{ newTeam =>
      val newTeamId = Accessor.addTeam(newTeam)
      Ok(Json.toJson(Map("id" -> newTeamId)))
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def deleteTeam(teamId: Int) = Action { implicit request =>
    Accessor.deleteTeam(teamId)
    Ok("")
  }

  def updateTeam(teamId: Int) = Action(parse.json) { implicit request =>
    request.body.validate[UpdateTeam].map{ updatedTeam =>
      Accessor.updateTeam(teamId, updatedTeam)
      Ok("")
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def getTeams = Action {
    Ok(Json.toJson(Accessor.getTeams))
  }
}
