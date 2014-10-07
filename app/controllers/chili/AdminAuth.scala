package controllers.chili 

import play.api.Play.current
import securesocial.core.{ Authorization, Identity }
import models.MyIdentity

class ChiliAdminAuth extends Authorization {
  def isAuthorized(user: Identity) = {
    val userEmail = user.asInstanceOf[MyIdentity].userInfo.email
    ChiliAdminAuth.administratorEmails.contains(userEmail)
  }
}

object ChiliAdminAuth {
  lazy val administratorEmails = 
    play.api.Play.configuration.getStringList("chili.administrators").get
}
