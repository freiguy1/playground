package models.chili

import java.util.Date

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

case class Vote(
  entryId: Int,
  voterName: Option[String],
  comment: Option[String],
  time: java.util.Date
)

case class Entry (
  entryId: Int,
  name: String,
  number: Int,
  chefName: String,
  spicyLevel: String,
  description: Option[String]
)

object Accessor {
  lazy private val database = Database.forDataSource(DB.getDataSource("default"))
  lazy private val entryTable = TableQuery[db.EntryTable]
  lazy private val voteTable = TableQuery[db.VoteTable]
  
  /*****************
   *     VOTES     *
   *****************/

  def addVote(newVote: Vote): Unit = database.withDynSession {
    val time = new java.sql.Timestamp(newVote.time.getTime())
    voteTable += db.Vote(0, newVote.entryId, newVote.voterName, newVote.comment, time)
  }

  def getVotes(): Seq[Vote] = database.withDynSession {
    voteTable.list.map(row => Vote(row.entryId, row.voterName, row.comment, row.time))
  }

  def getVotesByEntry(entryId: Int): Seq[Vote] = database.withDynSession {
    voteTable.filter(_.entryId === entryId).list
      .map(row => Vote(row.entryId, row.voterName, row.comment, row.time))
  }

  /*****************
   *    ENTRIES    *
   *****************/
  def addEntry(newEntry: Entry): Int = database.withDynSession {
    entryTable returning entryTable.map(_.entryId) += 
      db.Entry(0, newEntry.name, newEntry.number, newEntry.chefName, newEntry.spicyLevel, newEntry.description)
  }

  def getEntries(): Seq[Entry] = database.withDynSession {
    //teamTable.list.map(row => Team(row.teamId, row.name, row.captainName))
    entryTable.list.map(row => Entry(row.entryId, row.name, row.number, row.chefName, row.spicyLevel, row.description))
  }

  def updateEntry(entry: Entry): Unit = database.withDynSession {
    entryTable
      .filter(_.entryId === entry.entryId)
      .update(db.Entry(entry.entryId, entry.name, entry.number, entry.chefName, entry.spicyLevel, entry.description))
  }

  def getEntry(entryId: Int): Option[Entry] = database.withDynSession {
    entryTable.filter(_.entryId === entryId)
      .firstOption
      .map(row => Entry(row.entryId, row.name, row.number, row.chefName, row.spicyLevel, row.description))
  }

  def deleteEntry(entryId: Int): Unit = database.withDynSession {
    voteTable.filter(_.entryId === entryId).delete
    entryTable.filter(_.entryId === entryId).delete
  }

}
