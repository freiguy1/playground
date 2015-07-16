package controllers.vegas

import play.api.libs.json._
import models.vegas._

package object JsonConverters{
  
  implicit val newTeamFormat = Json.format[NewTeam]
  implicit val teamFormat = Json.format[Team]
  implicit val updateTeamFormat = Json.format[UpdateTeam]

}
