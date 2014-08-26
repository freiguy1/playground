package models.chili.db

import scala.slick.driver.MySQLDriver.simple._


private [chili]
case class Vote(
  voteId: Int,
  entryId: Int,
  voterName: Option[String],
  comment: Option[String],
  time: java.sql.Timestamp
)


private[chili]
class VoteTable(tag: Tag) extends Table[Vote](tag, "vote") {
  def voteId = column[Int]("voteId", O.PrimaryKey, O.AutoInc)
  def entryId = column[Int]("entryId", O.NotNull)
  def voterName = column[Option[String]]("voterName")
  def comment = column[Option[String]]("comment")
  def time = column[java.sql.Timestamp]("time")

  def * = (voteId, entryId, voterName, comment, time) <> (Vote.tupled, Vote.unapply)

  private lazy val entryTable = TableQuery[EntryTable]

  def entry = foreignKey("vote_entryId", entryId, entryTable)(_.entryId)
}

