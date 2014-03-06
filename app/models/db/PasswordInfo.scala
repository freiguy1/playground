package models.db

import scala.slick.driver.MySQLDriver.simple._

private[models]
case class PasswordInfo(passwordInfoId: Option[Int], hasher: String, password: String, salt: Option[String])

private[models]
class PasswordInfoTable(tag: Tag) extends Table[PasswordInfo](tag, "passwordInfo") {
  def passwordInfoId = column[Int]("passwordInfoId", O.PrimaryKey, O.AutoInc)
  def hasher = column[String]("hasher")
  def password = column[String]("password")
  def salt = column[Option[String]]("salt")
  
  def * = (passwordInfoId.?, hasher, password, salt) <> (PasswordInfo.tupled, PasswordInfo.unapply)
}
