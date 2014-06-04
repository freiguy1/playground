package models.mcisCup.db

import scala.slick.driver.MySQLDriver.simple._

private [mcisCup]
case class NextCompetitionInfo(
  nextCompetitionInfoId: Option[Int],
  name: String,
  details: Option[String],
  month: String
)
/*
CREATE TABLE nextCompetitionInfo (
    nextCompetitionInfoId INT AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    details VARCHAR(2048) NULL,
    month VARCHAR(20) NOT NULL,
    PRIMARY KEY(nextCompetitionInfo)
);
*/

private[mcisCup]
class NextCompetitionInfoTable(tag: Tag) extends Table[NextCompetitionInfo](tag, "nextCompetitionInfo"){
  def nextCompetitionInfoId = column[Int]("nextCompetitionInfoId", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name", O.NotNull)  
  def details = column[Option[String]]("details")
  def month = column[String]("month", O.NotNull)
  
  def * = (nextCompetitionInfoId.?, name, details, month) <> (NextCompetitionInfo.tupled, NextCompetitionInfo.unapply)
}

