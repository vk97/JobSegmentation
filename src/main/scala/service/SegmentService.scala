package service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.{Client, Job, JobGroup, Publisher, Rule}
import org.json4s.jackson.JsonMethods._

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class SegmentService {
  import org.json4s.DefaultFormats
  import org.json4s.native.Serialization
  implicit val system: ActorSystem = ActorSystem("akka-http-rest-server")
  private implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  private implicit val materialize: ActorMaterializer = ActorMaterializer()

  implicit val serialization = Serialization
  implicit val formats = DefaultFormats

  val service = new JobService
  val pubService = new PublisherService
  val groupService = new JobGroupService
def startSegment()={
  val client = Client("1","name","https://job-feeds-devlocal.s3.amazonaws.com/10-jobs.json",List())

  val publisher1 = Publisher("1",true,"",List())
  val publisher2 = Publisher("2",true,"",List())

  val publishers = List(publisher1,publisher2)
  pubService.insertPublisher(publishers)
  val rule1=Rule("department","equals","Recruitment")
  val rule2=Rule("department","equals","Engineering")
  val rule3=Rule("department","equals","Support")

  val jobGroup1 = JobGroup("1",List(),List(),List())
  val jobGroup2 = JobGroup("2",List(),List(),List())
  val jobGroupDefault = JobGroup("0",List(),List(),List())

  val jobGroups1 = List(jobGroup1,jobGroup2,jobGroupDefault)

  val jobGroup11 = JobGroup("1",List(rule1,rule2),List(publisher1),List())
  val jobGroup21 = JobGroup("2",List(rule3),List(publisher2),List())
  val jobGroupDefault1 = JobGroup("0",List(),List(),List())

  val jobGroups = List(jobGroup11,jobGroup21,jobGroupDefault1)
  groupService.insertGroups(jobGroups1)

  groupService.addPubs("0",List(publisher2,publisher1))
  groupService.addPubs("1",List(publisher1))
  groupService.addPubs("2",List(publisher2))

  groupService.addRules("1",List(Rule("department","equals","Recruitment")))
  groupService.addRules("2",List(rule3))

  def getEntity[T](path: String)(implicit m: Manifest[T]): List[Job] = {
    val entitySrc = scala.io.Source.fromURL(path)
    val entityStr = try entitySrc.mkString finally entitySrc.close()
    val p = parse(entityStr)
    p.extract[List[Job]]
  }
  lazy val jobsList = getEntity[Job]("https://job-feeds-devlocal.s3.amazonaws.com/10-jobs.json")
  def checkRule(job: Job,rule: Rule): Boolean ={
    val value = rule.field match {
      case "title" => job.title
      case "city" => job.city
      case "department" => job.department
    }
    rule.operator match{
      case "equals" =>{
        value== rule.value
      }
      case "notEquals" =>{
        value!= rule.value
      }
    }
  }
  service.insertJobs(jobsList)

  jobsList.foreach{job => groupService.addJobsToGroup("0",job)
    service.updateGroupOfJob(job,"0")}

  jobsList.foreach(job=>{
      jobGroups.foreach( jg=> {
        jg.rules.foreach( rule=>{
          if(checkRule(job,rule)) {
            service.updateGroupOfJob(job,jg.id)
            groupService.addJobsToGroup(jg.id,job)
          }
        })
      })
  })
  publishers.filter(_.isActive).foreach(p =>{
    jobGroups.foreach(jg=>
      if(jg.sponsoredPublishers.contains(p)){
        val jobs = groupService.getJobGroup(jg.id)
       jobs.foreach{ j =>  pubService.insertJobsToPublisher(p.id,j)}
      }
    )
  })
  def getPub(pId:String):Future[Option[Publisher]] ={
    pubService.getPub(pId)
  }
//  publishers.map(p=>{
//    val maybePub:Future[Option[Publisher]]= getPub(p.id)
//    maybePub.onComplete {
//      case Success(p) => println(p.get.id + ":" + p.get.jobs)
//      case Failure(t) => println(maybePub)
//    }
//  })
  pubService.getPublishers()
}
}
