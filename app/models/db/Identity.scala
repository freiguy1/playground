package models.db

import scala.slick.driver.MySQLDriver.simple._

private[models]
case class Identity(
    providerId: String,
    userSSId: String,
    firstName: String,
    lastName: String,
    fullName: String,
    email: Option[String],
    avatarUrl: Option[String],
    oAuth1InfoId: Option[Int],
    oAuth2InfoId: Option[Int],
    passwordInfoId: Option[Int],
    authenticationMethod: String
    )     

private[models]
class IdentityTable(tag: Tag) extends Table[Identity](tag, "identity") {
  def providerId = column[String]("providerId", O.PrimaryKey)
  def userSSId = column[String]("userSSId", O.PrimaryKey)
  def firstName = column[String]("firstName")
  def lastName = column[String]("lastName")
  def fullName = column[String]("fullName")
  def email = column[Option[String]]("email")
  def avatarUrl = column[Option[String]]("avatarUrl")
  def oAuth1InfoId = column[Option[Int]]("oAuth1InfoId")
  def oAuth2InfoId = column[Option[Int]]("oAuth2InfoId")
  def passwordInfoId = column[Option[Int]]("passwordInfoId")
  def authenticationMethod = column[String]("authenticationMethod")
  
  def * = (providerId, userSSId, firstName, lastName, fullName, email, avatarUrl, oAuth1InfoId, oAuth2InfoId, passwordInfoId, authenticationMethod) <> (Identity.tupled, Identity.unapply);
  
  private lazy val userTable = TableQuery[UserTable]
  private lazy val oAuth1InfoTable = TableQuery[OAuth1InfoTable]
  private lazy val oAuth2InfoTable = TableQuery[OAuth2InfoTable]
  private lazy val passwordInfoTable = TableQuery[PasswordInfoTable]

  def user = foreignKey("identity_userSSId", userSSId, userTable)(_.userSSId)
  def oAuth1Info = foreignKey("identity_oAuth1InfoId", oAuth1InfoId, oAuth1InfoTable)(_.oAuth1InfoId)
  def oAuth2Info = foreignKey("identity_oAuth2InfoId", oAuth2InfoId, oAuth2InfoTable)(_.oAuth2InfoId)
  def passwordInfo = foreignKey("identity_passwordInfoId", passwordInfoId, passwordInfoTable)(_.passwordInfoId)
}
