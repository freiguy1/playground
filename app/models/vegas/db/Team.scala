package models.vegas.db

import scala.slick.driver.MySQLDriver.simple._

private [vegas]
case class Team(
  teamId: Option[Int],
  name: String,
  captainName: String,
  miles: Double
)

private[vegas]
class TeamTable(tag: Tag) extends Table[Team](tag, "vegasTeam"){
  def teamId = column[Int]("vegasTeamId", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def captainName = column[String]("captainName", O.NotNull)
  def miles = column[Double]("miles", O.NotNull)
  
  def * = (teamId.?, name, captainName, miles) <> (Team.tupled, Team.unapply)
}

