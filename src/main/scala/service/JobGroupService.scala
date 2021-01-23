package service

import akka.http.scaladsl.server.Directives._
import models.{Job, JobGroup, Publisher, Rule}
import org.mongodb.scala.Completed
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{addToSet, set}
import repo.Mongo

import scala.concurrent.Await.result
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Success

class JobGroupService {
  val DB= Mongo
  def getAllGroups() ={
    DB.jobGroupsCollection.find().toFuture()
  }
  def insertGroups(groups:List[JobGroup]):Future[Completed]={
     DB.jobGroupsCollection.insertMany(groups).toFuture()
  }
  def addRules(id:String,rules:List[Rule])={
    DB.jobGroupsCollection.findOneAndUpdate(equal("id",id),addToSet("rules",rules)).toFuture()
  }
  def addPubs(id:String,p:List[Publisher])={
    DB.jobGroupsCollection.findOneAndUpdate(equal("id",id),addToSet("sponsoredPublishers",p)).toFuture()
  }
  def insertGroup(): Unit ={
    val j = JobGroup(
      id ="12", rules= List(), sponsoredPublishers= List(),jobs=List()
    )
    DB.jobGroupsCollection.insertOne(j).toFuture()
  }
  def addJobsToGroup(id:String,job:Job)={
    DB.jobGroupsCollection.findOneAndUpdate(equal("id",id),addToSet("jobs",job)).toFuture()
  }
  def getJobGroup(id:String)={
   val maybeGroup = DB.jobGroupsCollection.find(equal("id",id)).headOption()
    val grp = Await.result(maybeGroup,2.second)
    if(grp.head!=None)grp.head.jobs
    else List()
  }
}
