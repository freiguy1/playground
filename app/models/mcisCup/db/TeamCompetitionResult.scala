package models.mcisCup.db

import scala.slick.driver.MySQLDriver.simple._

private [mcisCup]
case class TeamCompetitionResult(
  teamId: Int,
  competitionId: Int,
  pointsEarned: Int,
  notes: Option[String]
)


private[mcisCup]
class TeamCompetitionResultTable(tag: Tag) extends Table[TeamCompetitionResult](tag, "teamCompetitionResult"){
  def teamId = column[Int]("teamId", O.PrimaryKey)
  def competitionId = column[Int]("competitionId", O.PrimaryKey)
  def pointsEarned = column[Int]("pointsEarned")
  def notes = column[Option[String]]("notes")
  
  def * = (teamId, competitionId, pointsEarned, notes) <> (TeamCompetitionResult.tupled, TeamCompetitionResult.unapply)

  def team = foreignKey("teamCompetitionResult_teamId", teamId, teamTable)(_.teamId)
  def competition = foreignKey("teamCompetitionResult_competitionId", competitionId, competitionTable)(_.competitionId)

  private val teamTable = TableQuery[TeamTable]
  private val competitionTable = TableQuery[CompetitionTable]
}

