package service

import models.{Job, Publisher}
import org.mongodb.scala.model.Filters.equal
import repo.Mongo
import org.mongodb.scala.model.Updates.{addToSet, set}

import scala.concurrent.Future

class PublisherService {
  val DB=  Mongo
  def getPub(id:String):Future[Option[Publisher]] ={
    DB.publishersCollection.find(equal("id",id)).headOption()
  }
  def insertPublisher(p:List[Publisher]): Unit ={
    DB.publishersCollection.insertMany(p).toFuture()
  }
  def insertJobsToPublisher(id:String,job:Job)= {
    DB.publishersCollection.updateOne(equal("id",id),addToSet("jobs",job)).toFuture()
  }
  def getPublishers(): Future[Seq[Publisher]] ={
    DB.publishersCollection.find().toFuture()
  }
}
