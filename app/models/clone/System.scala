package models.clone

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

case class System(
  systemId: Option[Long],
  name: String
)


object System {
  lazy private val cloneDb = Database.forDataSource(DB.getDataSource("clone"))
  lazy private val systemTable = TableQuery[db.SystemTable]

  def get = cloneDb.withDynSession {
    val query = for(system <- systemTable) yield system
    query.list.map(s => System(s.systemId, s.name))
  }

}
