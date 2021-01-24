package api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import org.json4s.native.Json
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

  val segmentService = new SegmentService
  def showSegmentedJobs()={
    Future(new SegmentService)

  }
  val routes:Route = concat(pathPrefix("segment"){
    val mayBeAns = segmentService.startSegment()
    onComplete(mayBeAns){
      case Success(list) => complete(list.map(pub => pub.id -> pub.jobs ))
      case Failure(exception) => {
        complete(StatusCodes.NotFound)
      }
    }
  })
}
