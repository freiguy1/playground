package controllers.soup 

import play.api.Play.current
import securesocial.core.{ Authorization, Identity }
import models.MyIdentity

class SoupAdminAuth extends Authorization {
  def isAuthorized(user: Identity) = {
    val userEmail = user.asInstanceOf[MyIdentity].userInfo.email
    SoupAdminAuth.administratorEmails.contains(userEmail)
  }
}

object SoupAdminAuth {
  lazy val administratorEmails = 
    play.api.Play.configuration.getStringList("soup.administrators").get
}
