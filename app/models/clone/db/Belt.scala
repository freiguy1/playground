package models.clone.db

import scala.slick.driver.MySQLDriver.simple._
import java.sql.Timestamp

private [clone]
case class Belt(
  beltId: Option[Long],
  systemId: Long,
  planetNum: String,
  beltNum: String,
  hasRats: Option[Boolean],
  lastChecked: Option[Timestamp],
  lastStatusChanged: Option[Timestamp]
)


private[clone]
class BeltTable(tag: Tag) extends Table[Belt](tag, "belt"){
  def beltId = column[Long]("beltId", O.PrimaryKey, O.AutoInc)
  def systemId = column[Long]("systemId", O.NotNull)
  def planetNum = column[String]("planetNum", O.NotNull)
  def beltNum = column[String]("beltNum", O.NotNull)
  def hasRats = column[Option[Boolean]]("hasRats", O.NotNull)
  def lastChecked = column[Option[Timestamp]]("lastChecked")
  def lastStatusChanged = column[Option[Timestamp]]("lastStatusChanged")

  def * = (beltId.?, systemId, planetNum, beltNum, hasRats, lastChecked, lastStatusChanged) <> (Belt.tupled, Belt.unapply)

  def system = foreignKey("belt_systemId", systemId, systemTable)(_.systemId)

  private val systemTable = TableQuery[SystemTable]
}

