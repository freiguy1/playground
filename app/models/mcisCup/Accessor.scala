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
  name: String
)

case class Competition(
  competitionId: Option[Int],
  name: String,
  instructions: String
)

case class TeamCompetitionResult(
  teamId: Option[Int],
  competitionId: Option[Int],
  pointsEarned: Int,
  notes: Option[String]
)

object Accessor {
  lazy private val defaultDb = Database.forDataSource(DB.getDataSource("default"))
  lazy private val teamTable = TableQuery[db.TeamTable]
  lazy private val competitionTable = TableQuery[db.CompetitionTable]
  lazy private val teamCompetitionResultTable = TableQuery[db.TeamCompetitionResultTable]

  //TEAMS
  def addTeam(newTeam: Team): Int = defaultDb.withDynSession {
    teamTable returning teamTable.map(_.teamId) += db.Team(None, newTeam.name)
  }
  
  def deleteTeam(teamId: Int): Unit = defaultDb.withDynSession {
    teamTable.filter(_.teamId === teamId).delete
  }
  

  //COMPETITIONS

  //TEAM COMPETITION RESULT

  /*def get = cloneDb.withDynSession {
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
  }*/

}
