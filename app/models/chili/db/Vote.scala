package models.chili.db

import scala.slick.driver.MySQLDriver.simple._


private [chili]
case class Vote(
  voteId: Int,
  entryId: Int,
  voterName: Option[String],
  comment: Option[String]
)


private[chili]
class VoteTable(tag: Tag) extends Table[Vote](tag, "vote") {
  def voteId = column[Int]("voteId", O.PrimaryKey, O.AutoInc)
  def entryId = column[Int]("entryId", O.NotNull)
  def voterName = column[Option[String]]("voterName")
  def comment = column[Option[String]]("comment")

  def * = (voteId, entryId, voterName, comment) <> (Vote.tupled, Vote.unapply)

  private lazy val entryTable = TableQuery[EntryTable]

  def entry = foreignKey("vote_entryId", entryId, entryTable)(_.entryId)
}

