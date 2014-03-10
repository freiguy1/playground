package controllers.balance

import play.api._
import play.api.mvc._

object Application extends Controller with securesocial.core.SecureSocial {

  def index = SecuredAction { implicit request => 
    val userInfo = request.user.asInstanceOf[models.MyIdentity].userInfo
    Ok(views.html.balance.index(models.balance.Balance.getByUserId(userInfo.userId)))
  }

  def addTransaction(amount: Double, note: Option[String]) = SecuredAction { implicit request =>
    val userInfo = request.user.asInstanceOf[models.MyIdentity].userInfo
    models.balance.Balance.addTransaction(userInfo.userId, amount, note) 
    Ok("Transaction recorded") 
  }
}
