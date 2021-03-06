package controllers.chili

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.data._
import play.api.data.validation._
import play.api.data.Forms._

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
    val result = Accessor.getVotesByEntry(entryId)
    Ok(Json.toJson(result))
  }

  def getEntries() = Action { implicit request => 
    Ok(Json.toJson(Accessor.getEntries.map(e => new EntryDto(e))))
  }

  def getEntry(entryId: Int) = Action { implicit request =>
    Accessor.getEntry(entryId).map(entry => 
      Ok(Json.toJson(new EntryDto(entry)))
    ).getOrElse(NotFound)
  }

  def addEntry = UserAwareAction(parse.json) { implicit request =>
    request.body.validate[EntryDto].map{ newEntry =>
      if(spicyLevels.contains(newEntry.spicyLevel)) {
        val newUuid = java.util.UUID.randomUUID.toString
        val newEntryId = Accessor.addEntry(newEntry.toEntry(0, newUuid))
        Ok(Json.toJson(Map("id" -> Json.toJson(newEntryId), "uuid" -> Json.toJson(newUuid))))
      } else {
        BadRequest("spicyLevel must be one of 'Mild', 'Medium', or 'Hot'")
      }
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def uuidUpdateEntry(uuid: String) = Action { implicit request => 
    Accessor.getEntryByUuid(uuid).map { dbEntry =>  
      val dto = new EntryDto(dbEntry)
      Ok(views.html.chili.update(uuid, entryForm.fill(dto)))
    }.getOrElse(NotFound)
  }

  def uuidUpdateEntrySubmit(uuid: String) = Action { implicit request =>
    entryForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.chili.update(uuid, formWithErrors))
      },
      updatedEntry => {
        Accessor.getEntryByUuid(uuid).map( dbEntry => { 
          Accessor.updateEntry(updatedEntry.toEntry(dbEntry.entryId, dbEntry.uuid))
          Redirect(controllers.chili.routes.Application.index)
        }).getOrElse(NotFound)
      }
    )
  }

  // PRIVATE ACTIONS -----------------

  def admin = SecuredAction(new ChiliAdminAuth) { implicit request =>
    Ok(views.html.chili.admin());
  }

  def results = SecuredAction(new ChiliAdminAuth) { implicit request =>
    val entries = Accessor.getEntries
    val votes = Accessor.getVotes
    val results = entries
      .map(entry => (entry.name, entry.number, votes.count(_.entryId == entry.entryId), votes.filter(vote => vote.entryId == entry.entryId && vote.comment.exists(_.trim.nonEmpty)).map(_.comment).flatten, entry.chefName));
    Ok(views.html.chili.results(results.sortBy(r => 0 - r._3)));
  }

  def getAdminEntries() = SecuredAction(new ChiliAdminAuth) { implicit request => 
    Ok(Json.toJson(Accessor.getEntries))
  }

  def clearVotes() = SecuredAction(new ChiliAdminAuth) { implicit request => 
    Accessor.clearVotes
    NoContent
  }

  def updateEntry(entryId: Int) = SecuredAction(true, new ChiliAdminAuth)(parse.json) { implicit request => 
    request.body.validate[EntryDto].map { updatedEntry =>
      if(spicyLevels.contains(updatedEntry.spicyLevel)) {
        Accessor.getEntry(entryId).map( dbEntry => 
          Accessor.updateEntry(updatedEntry.toEntry(entryId, dbEntry.uuid)))
        NoContent
      } else {
        BadRequest("spicyLevel must be one of 'Mild', 'Medium', or 'Hot'")
      }
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }

  def deleteEntry(entryId: Int) = SecuredAction(true, new ChiliAdminAuth) { implicit request =>
    Accessor.deleteEntry(entryId)
    NoContent
  }

  val entryForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "number" -> number,
      "chefName" -> nonEmptyText,
      "spicyLevel" -> nonEmptyText,
      "description" -> text
    )
    ((name, number, chefName, spicyLevel, description) => EntryDto.apply(None, name, number, chefName, spicyLevel, if(description.isEmpty) None else Some(description)))
    (input => input match {
      case entry: EntryDto => Some((entry.name, entry.number, entry.chefName, entry.spicyLevel, entry.description.getOrElse("")))
      case _ => None
    })
  )

  val decentSpicyLevel: Constraint[String] = Constraint("entry.spicyLevel")({ text =>
    if(spicyLevels.contains(text)){
      Valid
    } else {
      Invalid(Seq(ValidationError("Not a proper spicyLevel")))
    }
  })
}

case class EntryDto(
  entryId: Option[Int],
  name: String,
  number: Int,
  chefName: String,
  spicyLevel: String,
  description: Option[String]
) {

  def this(entry: Entry) = this(Some(entry.entryId), entry.name, entry.number, entry.chefName, entry.spicyLevel, entry.description)

  def toEntry(entryId: Int, uuid: String): Entry =
    Entry(entryId, this.name, this.number, this.chefName, this.spicyLevel, uuid, this.description)
}

case class VoteDto(
  voterName: Option[String],
  comment: Option[String]
) {
  def toVote(entryId: Int, date: java.util.Date): Vote =
    Vote(entryId, this.voterName, this.comment, date)
}

