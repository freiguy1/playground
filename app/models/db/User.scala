package models.db

import scala.slick.driver.MySQLDriver.simple._

private [models]
case class User(userId: Option[Int], email: String)

private [models]
class UserTable(tag: Tag) extends Table[User](tag, "user"){
  def userId = column[Int]("userId", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email")
  
  def * = (userId.?, email) <> (User.tupled, User.unapply)
}
