package controllers.chili

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._

import models.chili._
import models.MyIdentity

object Application extends Controller with securesocial.core.SecureSocial {
  implicit val entryFormat = Json.format[Entry]
  implicit val entryDtoFormat = Json.format[EntryDto]
  implicit val voteFormat = Json.format[Vote]
  implicit val voteDtoFormat = Json.format[VoteDto]

  lazy val administratorEmails = 
    play.api.Play.configuration.getStringList("chili.administrators").get

  def index = Action { implicit request =>
    Ok(views.html.chili.index())
  }

  // Use this in production
  /*def admin = SecuredAction { implicit request =>
    val userEmail = request.user.asInstanceOf[MyIdentity].userInfo.email
    if(administratorEmails.contains(userEmail))
      Ok(views.html.chili.admin());
    else Forbidden
  }*/

  def admin = UserAwareAction { implicit request =>
    Ok(views.html.chili.admin());
  }

  def addVote(entryId: Int) = Action(parse.json) { implicit request =>
    request.body.validate[VoteDto].map{ newVote =>
      Accessor.addVote(newVote.toVote(entryId, new java.util.Date()))
      NoContent
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def getVotesByEntry(entryId: Int) = Action { implicit request => 
    Ok(Json.toJson(Accessor.getVotesByEntry(entryId)))
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
}

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

case class VoteDto(
  voterName: Option[String],
  comment: Option[String]
) {
  def toVote(entryId: Int, date: java.util.Date): Vote =
    Vote(entryId, this.voterName, this.comment, date)
}
