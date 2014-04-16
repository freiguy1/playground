package models.mcisCup

import java.util.Date

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

case class Team(
  teamId: Option[Int],
  name: String,
  captainName: String
)

case class Competition(
  competitionId: Option[Int],
  name: String,
  instructions: String
)

case class TeamCompetitionResult(
  teamId: Int,
  competitionId: Int,
  pointsEarned: Int,
  notes: Option[String]
)

object Accessor {
  lazy private val defaultDb = Database.forDataSource(DB.getDataSource("default"))
  lazy private val teamTable = TableQuery[db.TeamTable]
  lazy private val competitionTable = TableQuery[db.CompetitionTable]
  lazy private val teamCompetitionResultTable = TableQuery[db.TeamCompetitionResultTable]

  //---------------------------------
  //TEAMS
  //---------------------------------

  def addTeam(newTeam: Team): Int = defaultDb.withDynSession {
    teamTable returning teamTable.map(_.teamId) += db.Team(None, newTeam.name, newTeam.captainName)
  }
  
  def deleteTeam(teamId: Int): Unit = defaultDb.withDynSession {
    teamCompetitionResultTable.filter(_.teamId === teamId).delete
    teamTable.filter(_.teamId === teamId).delete
  }
  
  def updateTeam(updatedTeam: Team): Unit = defaultDb.withDynSession {
    require(updatedTeam.teamId.isDefined)
    teamTable.filter(_.teamId === updatedTeam.teamId).update(db.Team(updatedTeam.teamId, updatedTeam.name, updatedTeam.captainName))
  }

  def getTeam(teamId: Int): Option[Team] = defaultDb.withDynSession {
    teamTable.filter(_.teamId === teamId).firstOption.map(row => Team(row.teamId, row.name, row.captainName))
  }

  def getTeams: Seq[Team] = defaultDb.withDynSession {
    teamTable.list.map(row => Team(row.teamId, row.name, row.captainName))
  }

  //---------------------------------
  //COMPETITIONS
  //---------------------------------
  
  def addCompetition(newCompetition: Competition): Int = defaultDb.withDynSession {
    competitionTable returning competitionTable.map(_.competitionId) += 
      db.Competition(None, newCompetition.name, newCompetition.instructions)
  }

  def updateCompetition(updatedCompetition: Competition): Unit = defaultDb.withDynSession {
    require(updatedCompetition.competitionId.isDefined)
    competitionTable.filter(_.competitionId === updatedCompetition.competitionId)
      .update(db.Competition(updatedCompetition.competitionId, updatedCompetition.name, updatedCompetition.instructions))
  }

  def getCompetition(competitionId: Int): Option[Competition] = defaultDb.withDynSession {
    competitionTable.filter(_.competitionId === competitionId).firstOption
      .map(row => Competition(row.competitionId, row.name, row.instructions))
  }

  def getCompetitions: Seq[Competition] = defaultDb.withDynSession {
    competitionTable.list.map(row => Competition(row.competitionId, row.name, row.instructions))
  }

  def deleteCompetition(competitionId: Int): Unit = defaultDb.withDynSession {
    teamCompetitionResultTable.filter(_.competitionId === competitionId).delete
    competitionTable.filter(_.competitionId === competitionId).delete
  }

  //---------------------------------
  //    TEAM COMPETITION RESULT
  //---------------------------------

  def getResult(teamId: Int, competitionId: Int): Option[TeamCompetitionResult] = defaultDb.withDynSession {
    teamCompetitionResultTable.filter(row => row.teamId === teamId && row.competitionId === competitionId).firstOption
      .map(row => TeamCompetitionResult(row.teamId, row.competitionId, row.pointsEarned, row.notes))
  }
  
  def getResults: Seq[TeamCompetitionResult] = defaultDb.withDynSession {
    teamCompetitionResultTable.list.map(row => TeamCompetitionResult(row.teamId, row.competitionId, row.pointsEarned, row.notes))
  }

  def addResult(result: TeamCompetitionResult): Unit = defaultDb.withDynSession {
    teamCompetitionResultTable +=
      db.TeamCompetitionResult(result.teamId, result.competitionId, result.pointsEarned, result.notes)
  }

  def updateResult(result: TeamCompetitionResult): Unit = defaultDb.withDynSession {
    teamCompetitionResultTable.filter(row => row.teamId === result.teamId && row.competitionId === result.competitionId)
      .update(db.TeamCompetitionResult(result.teamId, result.competitionId, result.pointsEarned, result.notes))
  }

}
