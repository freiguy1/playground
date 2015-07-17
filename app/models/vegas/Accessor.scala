package models.vegas

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

case class NewTeam(
  name: String,
  captainName: String
)

case class Team(
  id: Int,
  name: String,
  captainName: String,
  miles: Double
)

case class UpdateTeam(
  name: String,
  captainName: String,
  miles: Double
)

object Accessor {
  lazy private val defaultDb = Database.forDataSource(DB.getDataSource("default"))
  lazy private val teamTable = TableQuery[db.TeamTable]

  def addTeam(newTeam: NewTeam): Int = defaultDb.withDynSession {
    teamTable returning teamTable.map(_.teamId) += db.Team(None, newTeam.name, newTeam.captainName, 0.0)
  }

  def deleteTeam(teamId: Int): Unit = defaultDb.withDynSession {
    teamTable.filter(_.teamId === teamId).delete
  }
  
  def updateTeam(teamId: Int, updatedTeam: UpdateTeam): Unit = defaultDb.withDynSession {
    teamTable
      .filter(_.teamId === teamId)
      .update(db.Team(Some(teamId), updatedTeam.name, updatedTeam.captainName, updatedTeam.miles))
  }

  def getTeams: Seq[Team] = defaultDb.withDynSession {
    teamTable.list.map(row => Team(row.teamId.get, row.name, row.captainName, row.miles))
  }
}
