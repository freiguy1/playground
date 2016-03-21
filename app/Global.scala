
import play.api._
import scala.concurrent.duration._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    //initializeMcisStatusData()
    //Akka.system.scheduler.schedule(1 minutes, 1 minutes) {
      //everyMinute(app)
    //}
  }

  private def everyMinute(app: Application) {
    updateMcisStatusData()
  }

  private def updateMcisStatusData() {
    Logger.debug("Updating MCIS Status Data from data.sparkfun.com");
    models.mcisStatus.Log.update()
  }

  private def initializeMcisStatusData() {
    Logger.debug("Initializing MCIS Status Data from data.sparkfun.com");
    models.mcisStatus.Log.initialize()
  }
}
