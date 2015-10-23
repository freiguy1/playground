package controllers.mcisStatus

import java.time.{ ZoneOffset, OffsetDateTime }

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json._
import play.api.data._
import play.api.data.validation._
import play.api.data.Forms._

import models.mcisStatus._

object Application extends Controller with securesocial.core.SecureSocial {
  // implicit val entryFormat = Json.format[Entry]

  def index = Action { implicit request =>
    val mockedData = Seq(
      StatusSegment(false, OffsetDateTime.of(2015, 10, 19, 12, 0, 0, 0, ZoneOffset.UTC), 60, Some("Broken")),
      StatusSegment(true, OffsetDateTime.of(2015, 10, 19, 13, 0, 0, 0, ZoneOffset.UTC), 1380, None),
      StatusSegment(false, OffsetDateTime.of(2015, 10, 20, 12, 0, 0, 0, ZoneOffset.UTC), 60, Some("Broken")),
      StatusSegment(true, OffsetDateTime.of(2015, 10, 20, 13, 0, 0, 0, ZoneOffset.UTC), 1380, None),
      StatusSegment(false, OffsetDateTime.of(2015, 10, 21, 12, 0, 0, 0, ZoneOffset.UTC), 60, Some("Broken")),
      StatusSegment(true, OffsetDateTime.of(2015, 10, 21, 13, 0, 0, 0, ZoneOffset.UTC), 1380, None),
      StatusSegment(false, OffsetDateTime.of(2015, 10, 22, 12, 0, 0, 0, ZoneOffset.UTC), 60, Some("Broken")),
      StatusSegment(true, OffsetDateTime.of(2015, 10, 22, 13, 0, 0, 0, ZoneOffset.UTC), 1380, None),
      StatusSegment(false, OffsetDateTime.of(2015, 10, 23, 12, 0, 0, 0, ZoneOffset.UTC), 60, Some("Broken")),
      StatusSegment(true, OffsetDateTime.of(2015, 10, 23, 13, 0, 0, 0, ZoneOffset.UTC), 1380, None))
    Ok(views.html.mcisStatus.index(mockedData))
  }

}

case class StatusSegment(
  isAvailable: Boolean,
  startDateTime: OffsetDateTime,
  durationMinutes: Int,
  errorText: Option[String])

/*
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

*/
