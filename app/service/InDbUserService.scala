package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId


class InDbUserService(application: Application) extends UserServicePlugin(application) {

  // a simple User class that can have multiple identities
  case class User(id: String, identities: List[Identity])

  //
  var users = Map[String, User]()
  //private var identities = Map[String, Identity]()
  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    models.MyIdentity.findByIdentityId(id)
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    if ( Logger.isDebugEnabled ) {
      Logger.debug("users = %s".format(users))
    }
    models.MyIdentity.findByEmailAndProvider(email, providerId)
  }

  def save(user: Identity): Identity = {
    models.MyIdentity.saveOrUpdate(user)
  }

  /*def link(current: Identity, to: Identity) {
    val currentId = current.identityId.userId + "-" + current.identityId.providerId
    val toId = to.identityId.userId + "-" + to.identityId.providerId
    Logger.info(s"linking $currentId to $toId")

    val maybeUser = users.find {
      case (key, value) if value.identities.exists(_.identityId == current.identityId ) => true
    }

    maybeUser.foreach { u =>
      if ( !u._2.identities.exists(_.identityId == to.identityId)) {
        // do the link only if it's not linked already
        users = users + (u._1 -> User(u._1, to :: u._2.identities  ))
      }
    }
  }*/

  /***************************
   *       TOKEN STUFF       *
   ***************************/
  def save(token: Token) {
    tokens += (token.uuid -> token)
  }

  def findToken(token: String): Option[Token] = {
    tokens.get(token)
  }

  def deleteToken(uuid: String) {
    tokens -= uuid
  }

  def deleteTokens() {
    tokens = Map()
  }

  def deleteExpiredTokens() {
    tokens = tokens.filter(!_._2.isExpired)
  }
}
