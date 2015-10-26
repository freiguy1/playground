package models.db

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Timestamp


private[models]
case class Authenticator(
  authenticatorId: Int,
  identifier: String,
  userId: String,
  providerId: String,
  creationDate: Timestamp,
  lastUsed: Timestamp,
  expirationDate: Timestamp
)


private[models]
class AuthenticatorTable(tag: Tag) extends Table[Authenticator](tag, "authenticator") {
  def authenticatorId = column[Int]("authenticatorId", O.AutoInc, O.PrimaryKey)
  def identifier = column[String]("identifier")
  def userId = column[String]("userId")
  def providerId = column[String]("providerId")
  def creationDate = column[Timestamp]("creationDate")
  def lastUsed = column[Timestamp]("lastUsed")
  def expirationDate = column[Timestamp]("expirationDate")
  
  def * = (authenticatorId, identifier, userId, providerId, creationDate, lastUsed, expirationDate) <> (Authenticator.tupled, Authenticator.unapply);

}
