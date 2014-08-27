package controllers.chili 

import play.api.Play.current
import securesocial.core.{ Authorization, Identity }
import models.MyIdentity

class AdminAuth extends Authorization {
  def isAuthorized(user: Identity) = {
    val userEmail = user.asInstanceOf[MyIdentity].userInfo.email
    AdminAuth.administratorEmails.contains(userEmail)
  }
}

object AdminAuth {
  lazy val administratorEmails = 
    play.api.Play.configuration.getStringList("chili.administrators").get
}
