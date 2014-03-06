package controllers.clone

import play.api.libs.json._
import models.clone._

package object JsonConverters{
  
  implicit val systemFormat = Json.format[System]
  implicit val beltFormat = Json.format[Belt]

}
