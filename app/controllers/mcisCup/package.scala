package controllers.mcisCup

import play.api.libs.json._
import models.mcisCup._

package object JsonConverters{
  
  implicit val teamFormat = Json.format[Team]
  implicit val competitionFormat = Json.format[Competition]
  implicit val teamCompetitionResultFormat = Json.format[TeamCompetitionResult]

}
