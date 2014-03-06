package models.db

import scala.slick.driver.MySQLDriver.simple._

private[models]
case class OAuth1Info(oAuth1InfoId: Option[Int], token: String, secret: String)

private[models]
class OAuth1InfoTable(tag: Tag) extends Table[OAuth1Info](tag, "oAuth1Info") {
  def oAuth1InfoId = column[Int]("oAuth1InfoId", O.PrimaryKey, O.AutoInc)
  def token = column[String]("token")
  def secret = column[String]("secret")
  
  def * = (oAuth1InfoId.?, token, secret) <> (OAuth1Info.tupled, OAuth1Info.unapply)
}
