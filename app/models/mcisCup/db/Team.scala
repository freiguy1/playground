package models.mcisCup.db

import scala.slick.driver.MySQLDriver.simple._

private [mcisCup]
case class Team(
  teamId: Option[Int],
  name: String,
  captainName: String
)


private[mcisCup]
class TeamTable(tag: Tag) extends Table[Team](tag, "team"){
  def teamId = column[Int]("teamId", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def captainName = column[String]("captainName", O.NotNull)
  
  def * = (teamId.?, name, captainName) <> (Team.tupled, Team.unapply)
}

