package controllers.chili

import play.api._
import play.api.mvc._

import models.chili._
import play.api.libs.json._

case class EntryDto(
  name: String,
  number: Int,
  chefName: String,
  description: Option[String]
) {

  def this(entry: Entry) = this(entry.name, entry.number, entry.chefName, entry.description)

  def toEntry(entryId: Int): Entry =
    Entry(entryId, this.name, this.number, this.chefName, this.description)
}

object Application extends Controller with securesocial.core.SecureSocial {
  implicit val entryFormat = Json.format[Entry]
  implicit val entryDtoFormat = Json.format[EntryDto]
  implicit val voteFormat = Json.format[Vote]

  def index = Action { implicit request =>
    Ok(views.html.chili.index())
  }

  def addEntry = Action(parse.json) { implicit request =>
    request.body.validate[EntryDto].map{ newEntry =>
      val newEntryId = Accessor.addEntry(newEntry.toEntry(0))
      Ok(Json.toJson(Map("id" -> newEntryId)))
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def updateEntry(entryId: Int) = Action(parse.json) { implicit request => 
    request.body.validate[EntryDto].map { updatedEntry =>
      Accessor.updateEntry(updatedEntry.toEntry(entryId))
      NoContent
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def getEntries() = Action { implicit request => 
    Ok(Json.toJson(Accessor.getEntries))
  }

  def getEntry(entryId: Int) = Action { implicit request =>
    Accessor.getEntry(entryId).map(entry => 
      Ok(Json.toJson(new EntryDto(entry)))
    ).getOrElse(NotFound)
  }

  def deleteEntry(entryId: Int) = Action { implicit request =>
    Accessor.deleteEntry(entryId)
    NoContent
  }
/*
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
      NoContent
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def addNextCompetitionInfo = Action(parse.json) { implicit request =>
    request.body.validate[NextCompetitionInfo].map { nextCompetitionInfo =>
      Accessor.addNextCompetitionInfo(nextCompetitionInfo)
      NoContent
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def info = Action { implicit request =>
    val teams = Accessor.getTeams
    val competitions = Accessor.getCompetitions
    val results = Accessor.getResults
    val nextCompetitionInfo = Accessor.getNextCompetitionInfo
    Ok(Json.toJson(Map(
      "teams" -> Json.toJson(teams), 
      "competitions" -> Json.toJson(competitions), 
      "results" -> Json.toJson(results),
      "nextCompetitionInfo" -> Json.toJson(nextCompetitionInfo)
    )))
  }
*/
}

//case class ResultData(pointsEarned: Int, notes: Option[String])
