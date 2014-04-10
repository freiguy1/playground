package controllers.mcisCup

import play.api._
import play.api.mvc._

import controllers.mcisCup.JsonConverters._
import models.mcisCup._
import play.api.libs.json._

object Application extends Controller with securesocial.core.SecureSocial {

  def addteam = Action(parse.json) { implicit request =>
    request.body.validate[Team].map{ newTeam =>
      val newTeamId = Accessor.addTeam(newTeam)
      Ok(Json.toJson(Map("id" -> newTeamId)))
    }.recoverTotal(e => BadRequest("Detected Error: " + JsError.toFlatJson(e)))
  }
  
  def deleteTeam(teamId: Int) = Action { implicit request =>
    Accessor.deleteTeam(teamId)
    Ok("")
  }



/*  def index = SecuredAction { implicit request => 
    val userInfo = request.user.asInstanceOf[models.MyIdentity].userInfo
    Ok(views.html.balance.index(models.balance.Balance.getByUserId(userInfo.userId)))
  }

  def addTransaction(amount: Double, note: Option[String]) = SecuredAction(ajaxCall = true) { implicit request =>
    val userInfo = request.user.asInstanceOf[models.MyIdentity].userInfo
    models.balance.Balance.addTransaction(userInfo.userId, amount, note) 
    Ok(Json.toJson(models.balance.Balance.getByUserId(userInfo.userId)))
  }

  def getInfo = SecuredAction(ajaxCall = true) { implicit request =>
    val userInfo = request.user.asInstanceOf[models.MyIdentity].userInfo
    Ok(Json.toJson(models.balance.Balance.getByUserId(userInfo.userId)))
  }

  def clearTransactionsThrough(transactionId: Int) = SecuredAction(ajaxCall = true) { implicit request =>
    val userInfo = request.user.asInstanceOf[models.MyIdentity].userInfo
    models.balance.Balance.clearTransactionsThrough(transactionId, userInfo.userId)
    Ok("")
  }*/
}
