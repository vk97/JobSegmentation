package service

import models.Job
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import repo.Mongo

import scala.concurrent.Future

class JobService {
  val DB=  Mongo
  def insertJobs(jobs:List[Job])={
    DB.jobsCollection.insertMany(jobs).toFuture()
  }
  def updateGroupOfJob(job:Job,groupId:String): Future[Job] ={
    DB.jobsCollection.findOneAndUpdate(equal("title",job.title),set("groupId",groupId)).toFuture()
  }
}
