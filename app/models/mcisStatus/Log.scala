package models.mcisStatus

import play.api.Logger
import play.api.libs.ws._
import play.api.libs.json._
import play.api.Play.current
import play.api.cache.Cache

import java.time.{ LocalDateTime, OffsetDateTime }

import scala.collection.mutable.ArrayBuffer

case class Log(segments: Seq[StatusSegment], lastRefresh: LocalDateTime, lastTimestamp: OffsetDateTime)


case class StatusSegment(
  isAvailable: Boolean,
  startDateTime: OffsetDateTime,
  var durationMinutes: Int,
  errorText: Option[String])

object StatusSegment {
  def fromDataItem(startDateTime: OffsetDateTime, dataItem: DataItem): StatusSegment = {
    StatusSegment(
      StatusSegment.dataItemIsAvailable(dataItem),
      startDateTime,
      1,
      if(dataItem.info.isEmpty) None else Some(dataItem.info))

  }

  def dataItemIsAvailable(dataItem: DataItem): Boolean =
    dataItem.responsecode == "1"

}

object Log {

  implicit private lazy val entryFormat = Json.reads[DataItem]
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  lazy val phantPublicKey = play.api.Play.configuration.getString("mcisStaus.phantPublicKey").get
  lazy val url = s"https://data.sparkfun.com/output/$phantPublicKey.json"
  lazy val page1Url = s"$url?page=1"
  lazy val cacheKey = "mcisStatus.log"

  def initialize() {
    WS.url(url).get().map { response => 
      val parsedDataOpt = response.json.validate[Seq[DataItem]].asOpt
      if (parsedDataOpt.isEmpty) return
      val parsedData = parsedDataOpt.get
        .map(d => (d, OffsetDateTime.parse(d.timestamp)))
        .filter(d => d._2.isAfter(OffsetDateTime.now.minusDays(7)))
        .reverse

      val statusSegments = parsedData.foldLeft(new ArrayBuffer[StatusSegment]) { (segments, data) =>
        if (segments.isEmpty) {
          segments += StatusSegment.fromDataItem(data._2, data._1)
        } else if (segments.last.isAvailable != StatusSegment.dataItemIsAvailable(data._1)) {
          segments += StatusSegment.fromDataItem(data._2, data._1)
        } else {
          segments.last.durationMinutes = segments.last.durationMinutes + 1
          segments
        }
      }

      val log = Log(statusSegments, LocalDateTime.now, parsedData.last._2)
      Cache.set(cacheKey, log)
      println("added to cache")
    }
  }

  def get(): Log = {
    Cache.getAs[Log](cacheKey).getOrElse(Log(Seq(), LocalDateTime.now, OffsetDateTime.now))
  }

  def update() {
    val existingLogOpt = Cache.getAs[Log](cacheKey)
    if(existingLogOpt.isEmpty) Log.initialize()
    else {
      val existingLog = existingLogOpt
        .get
        .segments
        .filter(s => s.startDateTime.isAfter(OffsetDateTime.now.minusDays(7)))
      val latestLogSegmentStartTime = existingLog.last.startDateTime
      WS.url(page1Url).get().map { response => 
        val parsedDataOpt = response.json.validate[Seq[DataItem]].asOpt
        if (parsedDataOpt.isEmpty) return
        val parsedData = parsedDataOpt.get
          .map(d => (d, OffsetDateTime.parse(d.timestamp)))
          .filter(d => d._2.isAfter(existingLogOpt.get.lastTimestamp))
          .reverse

        val statusSegments = parsedData.foldLeft(existingLog.toBuffer) { (segments, data) =>
          if (segments.isEmpty) {
            segments += StatusSegment.fromDataItem(data._2, data._1)
          } else if (segments.last.isAvailable != StatusSegment.dataItemIsAvailable(data._1)) {
            segments += StatusSegment.fromDataItem(data._2, data._1)
          } else {
            segments.last.durationMinutes = segments.last.durationMinutes + 1
            segments
          }
        }

        // val log = Log(existingLog.dropRight(1) ++ statusSegments, LocalDateTime.now)
        val log = Log(statusSegments, LocalDateTime.now, parsedData.last._2)
        Cache.set(cacheKey, log)
      }
    }
  }
}

private case class DataItem(
  timestamp: String,
  info: String,
  responsecode: String,
  other: String)
