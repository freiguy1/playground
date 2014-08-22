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
  comment: Option[String]
)

case class Entry (
  entryId: Int,
  name: String,
  number: Int,
  chefName: String,
  description: Option[String]
)

object Accessor {
  lazy private val database = Database.forDataSource(DB.getDataSource("default"))
  lazy private val entryTable = TableQuery[db.EntryTable]
  lazy private val voteTable = TableQuery[db.VoteTable]

  def addEntry(newEntry: Entry): Int = database.withDynSession {
    entryTable returning entryTable.map(_.entryId) += db.Entry(0, newEntry.name, newEntry.number, newEntry.chefName, newEntry.description)
  }

  def getEntries(): Seq[Entry] = database.withDynSession {
    //teamTable.list.map(row => Team(row.teamId, row.name, row.captainName))
    entryTable.list.map(row => Entry(row.entryId, row.name, row.number, row.chefName, row.description))
  }

  def updateEntry(entry: Entry): Unit = database.withDynSession {
    entryTable
      .filter(_.entryId === entry.entryId)
      .update(db.Entry(entry.entryId, entry.name, entry.number, entry.chefName, entry.description))
  }

  def getEntry(entryId: Int): Option[Entry] = database.withDynSession {
    entryTable.filter(_.entryId === entryId)
      .firstOption
      .map(row => Entry(row.entryId, row.name, row.number, row.chefName, row.description))
  }

  def deleteEntry(entryId: Int): Unit = database.withDynSession {
    voteTable.filter(_.entryId === entryId).delete
    entryTable.filter(_.entryId === entryId).delete
  }

}
