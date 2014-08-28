package models.chili.db

import scala.slick.driver.MySQLDriver.simple._


private [chili]
case class Entry(
  entryId: Int,
  name: String,
  number: Int,
  chefName: String,
  spicyLevel: String,
  description: Option[String]
)


private[chili]
class EntryTable(tag: Tag) extends Table[Entry](tag, "entry"){
  def entryId = column[Int]("entryId", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)
  def number = column[Int]("number", O.NotNull)
  def chefName = column[String]("chefName", O.NotNull)
  def spicyLevel = column[String]("spicyLevel", O.NotNull)
  def description = column[Option[String]]("description")
  
  def * = (entryId, name, number, chefName, spicyLevel, description) <> (Entry.tupled, Entry.unapply)
}

