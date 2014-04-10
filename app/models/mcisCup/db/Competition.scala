package models.mcisCup.db

import scala.slick.driver.MySQLDriver.simple._

private [mcisCup]
case class Competition(
  competitionId: Option[Int],
  name: String,
  instructions: String
)


private[mcisCup]
class CompetitionTable(tag: Tag) extends Table[Competition](tag, "competition"){
  def competitionId = column[Int]("competitionId", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def instructions = column[String]("instructions", O.NotNull)
  
  def * = (competitionId.?, name, instructions) <> (Competition.tupled, Competition.unapply)
}

