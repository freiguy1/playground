package controllers.mcisCup

import play.api._
import play.api.mvc._

import controllers.mcisCup.JsonConverters._
import models.mcisCup._
import play.api.libs.json._

object Application extends Controller with securesocial.core.SecureSocial {

  def index = Action { implicit request =>
    Ok(views.html.mcisCup.index())
  }

  def admin = Action { implicit request =>
    Ok(views.html.mcisCup.admin())
  }

  def addTeam = Action(parse.json) { implicit request =>
    request.body.validate[Team].map{ newTeam =>
      val newTeamId = Accessor.addTeam(newTeam)
      Ok(Json.toJson(Map("id" -> newTeamId)))
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }
  
  def deleteTeam(teamId: Int) = Action { implicit request =>
    Accessor.deleteTeam(teamId)
    Ok("")
  }

  def updateTeam(teamId: Int) = Action(parse.json) { implicit request =>
    request.body.validate[Team].map{ updatedTeam =>
      Accessor.updateTeam(updatedTeam.copy(teamId = Some(teamId)))
      Ok("")
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def addCompetition = Action(parse.json) { implicit request =>
    request.body.validate[Competition].map{ newCompetition =>
      val newCompetitionId = Accessor.addCompetition(newCompetition)
      Ok(Json.toJson(Map("id" -> newCompetitionId)))
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }
  
  def deleteCompetition(competitionId: Int) = Action { implicit request =>
    Accessor.deleteCompetition(competitionId)
    Ok("")
  }

  def updateCompetition(competitionId: Int) = Action(parse.json) { implicit request =>
    request.body.validate[Competition].map{ updatedCompetition =>
      Accessor.updateCompetition(updatedCompetition.copy(competitionId = Some(competitionId)))
      Ok("")
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  implicit val resultDataFormat = Json.format[ResultData]

  def addOrUpdateResult(competitionId: Int, teamId: Int) = Action(parse.json) { implicit request =>
    request.body.validate[ResultData].map { result =>
      Accessor.getResult(teamId, competitionId)
        .map(_ => Accessor.updateResult(TeamCompetitionResult(teamId, competitionId, result.pointsEarned, result.notes)))
        .getOrElse(Accessor.addResult(TeamCompetitionResult(teamId, competitionId, result.pointsEarned, result.notes)))
      Ok("")
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def info = Action { implicit request =>
    val teams = Accessor.getTeams
    val competitions = Accessor.getCompetitions
    val results = Accessor.getResults
    /*val tuples = for(competition <- competitions; team <- teams){
      val result = results.find(r => r.teamId == team.teamId.get && r.competitionId == competition.competitionId.get)
      yield(competition.competitionId, team.teamId, result)
    }

    val data = competitions
      .map(c => c.competitionId.get -> teams
        .map(t => t.teamId.get -> results
          .find(r => r.teamId == t.teamId.get && r.competitionId == c.competitionId.get))
        .toMap)
      .toMap
    */

    Ok(Json.toJson(Map("teams" -> Json.toJson(teams), "competitions" -> Json.toJson(competitions), "results" -> Json.toJson(results))))
  }

}

case class ResultData(pointsEarned: Int, notes: Option[String])
