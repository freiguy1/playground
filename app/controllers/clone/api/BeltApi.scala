package controllers.clone.api

import play.api.libs.json._
import play.api.mvc._

import controllers.clone.JsonConverters._

import models.clone._

object BeltApi extends Controller {

  def get() = Action { request =>
    request.cookies.get("itsMe").map(c =>
      Ok(Json.toJson(Belt.get))
    ).getOrElse(Ok(Json.toJson(Belt.get.map(b => b.copy(hasRats = None, lastChecked = None, lastStatusChanged = None)))))
  }

  def hasRats(beltId: Long) = Action { request => 
    Belt.get(beltId).map{belt => 
      val lastChecked = Some(new java.util.Date())
      val lastStatusChanged = if(belt.hasRats.exists(_ == true)) belt.lastStatusChanged else lastChecked
      Belt.update(belt.copy(hasRats = Some(true), lastChecked = lastChecked, lastStatusChanged = lastStatusChanged))
      Ok("Completed")
    }.getOrElse(NotFound("Sorry, not found!"))
  }
  
  def isEmpty(beltId: Long) = Action { request => 
    Belt.get(beltId).map{belt => 
      val lastChecked = Some(new java.util.Date())
      val lastStatusChanged = if(belt.hasRats.exists(_ == false)) belt.lastStatusChanged else lastChecked
      Belt.update(belt.copy(hasRats = Some(false), lastChecked = lastChecked, lastStatusChanged = lastStatusChanged))
      Ok("Completed")
    }.getOrElse(NotFound("Sorry, not found!"))
  }

  def clear(beltId: Long) = Action { request => 
    Belt.get(beltId).map{belt => 
      Belt.update(belt.copy(hasRats = None, lastChecked = None, lastStatusChanged = None))
      Ok("Completed")
    }.getOrElse(NotFound("Sorry, not found!"))
  }
}
