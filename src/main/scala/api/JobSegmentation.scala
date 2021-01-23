package api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import service.SegmentService

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class JobSegmentation {
//  val service = new SegmentService
  import org.json4s.DefaultFormats
  import org.json4s.native.Serialization
  implicit val system: ActorSystem = ActorSystem("akka-http-rest-server")
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  private implicit val materialize: ActorMaterializer = ActorMaterializer()
  import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

  implicit val serialization = Serialization
  implicit val formats = DefaultFormats

  def showSegmentedJobs()={
    Future(new SegmentService)

  }
  val routes:Route = concat(pathPrefix("seg"){
    onComplete(showSegmentedJobs()){
      case Success(seg) => complete("done")
      case Failure(exception) => {
        complete(StatusCodes.NotFound)
      }
    }
  })
}
