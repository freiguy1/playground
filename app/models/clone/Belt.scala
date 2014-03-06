package models.clone

import java.util.Date

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

case class Belt(
  beltId: Option[Long],
  systemId: Long,
  planetNum: String,
  beltNum: String,
  hasRats: Option[Boolean],
  lastChecked: Option[Date],
  lastStatusChanged: Option[Date]
)


object Belt {
  lazy private val cloneDb = Database.forDataSource(DB.getDataSource("clone"))
  lazy private val beltTable = TableQuery[db.BeltTable]

  def get = cloneDb.withDynSession {
    val query = for(dbBelt <- beltTable) yield dbBelt
    query.list.map(b => Belt(b.beltId, b.systemId, b.planetNum, b.beltNum, b.hasRats, b.lastChecked, b.lastStatusChanged))
  }

  def get(id: Long) = cloneDb.withDynSession {
    val query = for(dbBelt <- beltTable if dbBelt.beltId === id) yield dbBelt
    query.firstOption.map(b => Belt(b.beltId, b.systemId, b.planetNum, b.beltNum, b.hasRats, b.lastChecked, b.lastStatusChanged))
  }

  def update(updatedBelt: Belt): Unit = cloneDb.withDynSession {
    require(updatedBelt.beltId.isDefined)
    val query = for(dbBelt <- beltTable if dbBelt.beltId === updatedBelt.beltId) yield dbBelt 
    query.update(db.Belt(updatedBelt.beltId, 
                         updatedBelt.systemId, 
                         updatedBelt.planetNum, 
                         updatedBelt.beltNum, 
                         updatedBelt.hasRats, 
                         updatedBelt.lastChecked.map(d => new java.sql.Timestamp(d.getTime())), 
                         updatedBelt.lastStatusChanged.map(d => new java.sql.Timestamp(d.getTime()))
    ))
  }

}
