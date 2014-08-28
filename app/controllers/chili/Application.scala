package controllers.chili

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._

import models.chili._

object Application extends Controller with securesocial.core.SecureSocial {
  implicit val entryFormat = Json.format[Entry]
  implicit val entryDtoFormat = Json.format[EntryDto]
  implicit val voteFormat = Json.format[Vote]
  implicit val voteDtoFormat = Json.format[VoteDto]

  val spicyLevels = Seq("Mild", "Medium", "Hot")

  def index = Action { implicit request =>
    Ok(views.html.chili.index())
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

  def getEntries() = Action { implicit request => 
    Ok(Json.toJson(Accessor.getEntries))
  }

  def getEntry(entryId: Int) = Action { implicit request =>
    Accessor.getEntry(entryId).map(entry => 
      Ok(Json.toJson(new EntryDto(entry)))
    ).getOrElse(NotFound)
  }

  // PRIVATE
  // Use this in production
  /*def admin = SecuredAction { implicit request =>
    if(isAdmin(request.user))
      Ok(views.html.chili.admin());
    else Forbidden
  }*/

  // PRIVATE
  def admin = SecuredAction(new AdminAuth) { implicit request =>
    Ok(views.html.chili.admin());
  }


  // PRIVATE
  def addEntry = SecuredAction(true, new AdminAuth)(parse.json) { implicit request =>
    request.body.validate[EntryDto].map{ newEntry =>
      if(spicyLevels.contains(newEntry.spicyLevel)) {
        val newEntryId = Accessor.addEntry(newEntry.toEntry(0))
        Ok(Json.toJson(Map("id" -> newEntryId)))
      } else {
        BadRequest("spicyLevel must be one of 'Mild', 'Medium', or 'Hot'")
      }
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  // PRIVATE
  def updateEntry(entryId: Int) = SecuredAction(true, new AdminAuth)(parse.json) { implicit request => 
    request.body.validate[EntryDto].map { updatedEntry =>
      if(spicyLevels.contains(updatedEntry.spicyLevel)) {
        Accessor.updateEntry(updatedEntry.toEntry(entryId))
        NoContent
      } else {
        BadRequest("spicyLevel must be one of 'Mild', 'Medium', or 'Hot'")
      }
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  // PRIVATE
  def deleteEntry(entryId: Int) = SecuredAction(true, new AdminAuth) { implicit request =>
    Accessor.deleteEntry(entryId)
    NoContent
  }

}

case class EntryDto(
  name: String,
  number: Int,
  chefName: String,
  spicyLevel: String,
  description: Option[String]
) {

  def this(entry: Entry) = this(entry.name, entry.number, entry.chefName, entry.spicyLevel, entry.description)

  def toEntry(entryId: Int): Entry =
    Entry(entryId, this.name, this.number, this.chefName, this.spicyLevel, this.description)
}

case class VoteDto(
  voterName: Option[String],
  comment: Option[String]
) {
  def toVote(entryId: Int, date: java.util.Date): Vote =
    Vote(entryId, this.voterName, this.comment, date)
}

