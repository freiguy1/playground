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
        BadRequest("Bad, u bad")
        //BadRequest(views.html.user(formWithErrors))
      },
      updatedEntry => {
        Accessor.getEntryByUuid(uuid).map( dbEntry => { 
          Accessor.updateEntry(updatedEntry.toEntry(dbEntry.entryId, dbEntry.uuid))
          Redirect(controllers.chili.routes.Application.index)
        }).getOrElse(NotFound)
      }
    )
  }

  // PRIVATE
  def admin = SecuredAction(new AdminAuth) { implicit request =>
    Ok(views.html.chili.admin());
  }


  // PRIVATE
  def updateEntry(entryId: Int) = SecuredAction(true, new AdminAuth)(parse.json) { implicit request => 
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

  // PRIVATE
  def deleteEntry(entryId: Int) = SecuredAction(true, new AdminAuth) { implicit request =>
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

