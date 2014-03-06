package models.clone.db

import scala.slick.driver.MySQLDriver.simple._

private [clone]
case class System(
  systemId: Option[Long],
  name: String
)


private [clone]
class SystemTable(tag: Tag) extends Table[System](tag, "system"){
  def systemId = column[Long]("systemId", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def * = (systemId.?, name) <> (System.tupled, System.unapply)
}

