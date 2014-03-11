package controllers.balance

import play.api.libs.json._
import models.balance._

package object JsonConverters{
  
  implicit val transactionFormat = Json.format[Transaction]
  implicit val balanceFormat = Json.format[Balance]

}
