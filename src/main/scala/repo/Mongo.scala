package repo

import models._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.bson.codecs.Macros

object Mongo {
  def customCodecRegistry() = {
    fromProviders(
      Macros.createCodecProvider[Publisher](),
    Macros.createCodecProvider[Client](),
    Macros.createCodecProvider[Job](),
    Macros.createCodecProvider[JobGroup]()
  )
  }
  val codecRegistry = fromRegistries(customCodecRegistry,DEFAULT_CODEC_REGISTRY)
  val DB:MongoDatabase = MongoClient().getDatabase("joveoDB").withCodecRegistry(codecRegistry)
  val clientsCollection:MongoCollection[Client] = DB.getCollection("clients")
  val jobsCollection:MongoCollection[Job] = DB.getCollection("jobs")
  val jobGroupsCollection:MongoCollection[JobGroup] = DB.getCollection("jobGroups")
  val publishersCollection:MongoCollection[Publisher] = DB.getCollection("publishers")
}
