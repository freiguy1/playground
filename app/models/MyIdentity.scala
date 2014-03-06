package models

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

// Use MySQLDriver to connect to a MySQL database
import scala.slick.driver.MySQLDriver.simple._

// Use the implicit dynamicSession
import Database.dynamicSession

import securesocial.core._

case class MyIdentity(
  identityId: IdentityId, firstName: String, lastName: String, fullName: String, email: Option[String],
  avatarUrl: Option[String], authMethod: AuthenticationMethod,
  userInfo: UserInfo,
  oAuth1Info: Option[OAuth1Info] = None,
  oAuth2Info: Option[OAuth2Info] = None,
  passwordInfo: Option[PasswordInfo] = None) extends Identity

case class UserInfo(
  userId: Int,
  email: String)
                      
object MyIdentity{
  
  private lazy val database = Database.forDataSource(DB.getDataSource("default"))
  private lazy val identityTable = TableQuery[db.IdentityTable]
  private lazy val userTable = TableQuery[db.UserTable]
  private lazy val oAuth1InfoTable = TableQuery[db.OAuth1InfoTable]
  private lazy val oAuth2InfoTable = TableQuery[db.OAuth2InfoTable]
  private lazy val passwordInfoTable = TableQuery[db.PasswordInfoTable]

  
  def findByIdentityId(identityId: IdentityId): Option[MyIdentity] = database.withDynSession {
    val query = for(dbIdentity <- identityTable
        if (dbIdentity.userSSId === identityId.userId && dbIdentity.providerId === identityId.providerId)) yield dbIdentity
    query.firstOption.map(dbIdentity => {
      Some(createMyIdentityFromDbIdentity(dbIdentity))
    }).getOrElse(None)
  }
  
  def findByEmailAndProvider(email: String, providerId: String): Option[MyIdentity] = database.withDynSession {
    val query = for(mscIdentity <- identityTable 
        if (mscIdentity.email === email && mscIdentity.providerId === providerId)) yield mscIdentity
    query.firstOption.map(dbIdentity => {
      Some(createMyIdentityFromDbIdentity(dbIdentity))
    }).getOrElse(None)
  }
  
  def saveOrUpdate(identity: Identity): Identity = database.withDynSession {
    val query = for(mscIdentity <- identityTable 
        if (mscIdentity.userSSId === identity.identityId.userId && mscIdentity.providerId === identity.identityId.providerId)) yield mscIdentity
    
    query.firstOption.map(dbIdentity => {
      // If user exists, update...
      // update oAuth1Info if needed
      identity.oAuth1Info.map(oAuth1Info => {
        val query = for(dbOAuth1Info <- oAuth1InfoTable if dbOAuth1Info.oAuth1InfoId === dbIdentity.oAuth1InfoId) yield dbOAuth1Info
        query.update(db.OAuth1Info(dbIdentity.oAuth1InfoId, oAuth1Info.token, oAuth1Info.secret))
      })
	    // update oAuth2Info if needed
	    identity.oAuth2Info.map(oAuth2Info => {
        val query = for(dbOAuth2Info <- oAuth2InfoTable if dbOAuth2Info.oAuth2InfoId === dbIdentity.oAuth2InfoId) yield dbOAuth2Info
        query.update(db.OAuth2Info(dbIdentity.oAuth2InfoId, oAuth2Info.accessToken, oAuth2Info.tokenType, oAuth2Info.expiresIn, oAuth2Info.refreshToken))
      })
      // update passwordInfo if needed
      identity.passwordInfo.map(passwordInfo => {
        val query = for(dbPasswordInfo <- passwordInfoTable if dbPasswordInfo.passwordInfoId === dbIdentity.passwordInfoId) yield dbPasswordInfo
        query.update(db.PasswordInfo(dbIdentity.passwordInfoId, passwordInfo.hasher, passwordInfo.password, passwordInfo.salt))
      })
	  
      // update identity
      val query = for(dbIdentity2 <- identityTable if(dbIdentity2.userSSId === dbIdentity.userSSId && dbIdentity2.providerId === dbIdentity.providerId)) yield dbIdentity2
      query.update(db.Identity(dbIdentity.providerId, dbIdentity.userSSId, identity.firstName, identity.lastName, identity.fullName, identity.email, identity.avatarUrl, dbIdentity.oAuth1InfoId, dbIdentity.oAuth2InfoId, dbIdentity.passwordInfoId, identity.authMethod.method))

      // fetch recently updated identity
      findByIdentityId(IdentityId(identity.identityId.userId, identity.identityId.providerId)).get
      
    }).getOrElse{
      // User doesn't exist, creating...
      require(identity.email.isDefined)
      // create user
      userTable insert db.User(None, identity.identityId.userId, identity.email.get)
      // create oAuth1Info
      val oAuth1InfoId : Option[Int] = identity.oAuth1Info.map(oAuth1Info =>{
        oAuth1InfoTable returning oAuth1InfoTable.map(_.oAuth1InfoId) insert new db.OAuth1Info(None, oAuth1Info.token, oAuth1Info.secret)
      })
      // create oAuth2Info
      val oAuth2InfoId : Option[Int] = identity.oAuth2Info.map(oAuth2Info =>{
        oAuth2InfoTable returning oAuth2InfoTable.map(_.oAuth2InfoId) insert new db.OAuth2Info(None, oAuth2Info.accessToken, oAuth2Info.tokenType, oAuth2Info.expiresIn, oAuth2Info.refreshToken)
      })
      // create passwordInfo
      val passwordInfoId : Option[Int] = identity.passwordInfo.map(passwordInfo =>{
        passwordInfoTable returning passwordInfoTable.map(_.passwordInfoId) insert new db.PasswordInfo(None, passwordInfo.hasher, passwordInfo.password, passwordInfo.salt)
      })

      
      //create identity using these new ids.
      identityTable insert db.Identity(identity.identityId.providerId, identity.identityId.userId, identity.firstName, identity.lastName, identity.fullName, identity.email, identity.avatarUrl, oAuth1InfoId, oAuth2InfoId, passwordInfoId, identity.authMethod.method)
      
      // fetch recently added identity
      findByIdentityId(IdentityId(identity.identityId.userId, identity.identityId.providerId)).get
      
    }
  }

  def updateUserInfo(updatedUserInfo: UserInfo): Unit = database.withDynSession {
    val query = for(dbUser <- userTable if dbUser.userId === updatedUserInfo.userId) yield dbUser
    query.firstOption.map(dbUser => {
      val updateQuery = for(thing <- userTable if thing.userId === updatedUserInfo.userId) yield thing
      updateQuery.update(db.User(Some(updatedUserInfo.userId), dbUser.userSSId, updatedUserInfo.email))
    }).getOrElse(throw new NoSuchElementException)
  }


  private def createMyIdentityFromDbIdentity(dbIdentity: db.Identity): MyIdentity = {
    val query = for(dbUser <- userTable if dbUser.userSSId === dbIdentity.userSSId) yield dbUser
    val dbUser = query.firstOption.get
    val userInfo = new UserInfo(
      dbUser.userId.get,
      dbIdentity.email.get
    )
    new MyIdentity(IdentityId(dbIdentity.userSSId, dbIdentity.providerId), dbIdentity.firstName, dbIdentity.lastName, dbIdentity.fullName, dbIdentity.email,
      dbIdentity.avatarUrl, AuthenticationMethod(dbIdentity.authenticationMethod), userInfo,
      getOAuth1InfoForIdentity(dbIdentity),
      getOAuth2InfoForIdentity(dbIdentity),
      getPasswordInfoForIdentity(dbIdentity)
    )
  }

  /**
   * Finds the oauth1info for an identity.  If the oauth1info property on the
   * identity is not null, it assumes that oauth1info exists.
   * 
   * @param identity - the identity retrieved from the database 
   */
  private def getOAuth1InfoForIdentity(identity: db.Identity): Option[OAuth1Info] = {
    identity.oAuth1InfoId.map(oAuth1InfoId => {
      val query = for(oAuth1Info <- oAuth1InfoTable if oAuth1Info.oAuth1InfoId === oAuth1InfoId) yield oAuth1Info
      val dbOAuth1Info = query.first()
      Some(securesocial.core.OAuth1Info(dbOAuth1Info.token, dbOAuth1Info.secret))
    }).getOrElse(None)
  }
  
  
  /**
   * Finds the oauth2info for an identity.  If the oauth2info property on the
   * identity is not null, it assumes that oauth2info exists.
   * 
   * @param identity - the identity retrieved from the database 
   */
  private def getOAuth2InfoForIdentity(identity: db.Identity): Option[OAuth2Info] = {
    identity.oAuth2InfoId.map(oAuth2InfoId => {
      val query = for(oAuth2Info <- oAuth2InfoTable if oAuth2Info.oAuth2InfoId === oAuth2InfoId) yield oAuth2Info
      val dbOAuth2Info = query.first()
      Some(securesocial.core.OAuth2Info(dbOAuth2Info.accessToken, dbOAuth2Info.tokenType, dbOAuth2Info.expiresIn, dbOAuth2Info.refreshToken))
    }).getOrElse(None)
  }
  
  
  /**
   * Finds the passwordInfo for an identity.  If the passwordInfo property on the
   * identity is not null, it assumes that passwordInfo exists.
   * 
   * @param identity - the identity retrieved from the database 
   */
  def getPasswordInfoForIdentity(identity: db.Identity): Option[PasswordInfo] = {
    identity.passwordInfoId.map(passwordInfoId => {
      val query = for(passwordInfo <- passwordInfoTable if passwordInfo.passwordInfoId === passwordInfoId) yield passwordInfo
      val dbPasswordInfoInfo = query.first()
      Some(PasswordInfo(dbPasswordInfoInfo.hasher, dbPasswordInfoInfo.password, dbPasswordInfoInfo.salt))
    }).getOrElse(None)
  }
}
