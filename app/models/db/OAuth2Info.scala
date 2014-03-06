package models.db

import scala.slick.driver.MySQLDriver.simple._

private[models]
case class OAuth2Info(oAuth2InfoId: Option[Int], accessToken: String, tokenType: Option[String], expiresIn: Option[Int], refreshToken: Option[String])

private[models]
class OAuth2InfoTable(tag: Tag) extends Table[OAuth2Info](tag, "oAuth2Info") {
  def oAuth2InfoId = column[Int]("oAuth2InfoId", O.PrimaryKey, O.AutoInc)
  def accessToken = column[String]("accessToken")
  def tokenType = column[Option[String]]("tokenType")
  def expiresIn = column[Option[Int]]("expiresIn")
  def refreshToken = column[Option[String]]("refreshToken")
  
  def * = (oAuth2InfoId.?, accessToken, tokenType, expiresIn, refreshToken) <> (OAuth2Info.tupled, OAuth2Info.unapply)
}
