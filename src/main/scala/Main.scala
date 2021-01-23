import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import api.JobSegmentation
import models.JobGroup
import repo.Mongo
import service.{JobGroupService, SegmentService}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.io.StdIn

object Main extends App{
  implicit val system: ActorSystem = ActorSystem("akka-http-rest-server")
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  private implicit val materialize: ActorMaterializer = ActorMaterializer()
  val routes = new JobSegmentation
//  new SegmentService
  val host = "127.0.0.1"
  val port = 8080
  val serverFuture = Http().bindAndHandle(routes.routes, host, port)
//  val DB=Mongo
//  Await.result(DB,3.second)
//  val service = new JobGroupService
//  service.insertGroups(List(JobGroup(
//    id ="21", rules= List(), sponsoredPublishers= List(),jobs=List()
//  ),JobGroup(
//    id ="22", rules= List(), sponsoredPublishers= List(),jobs=List()
//  )))
  println(s"Server is online at port = $port, PRESS ENTER TO EXIT")
  StdIn.readLine()
  serverFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
