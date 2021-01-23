package models

case class Job(title: String="", company: String="", city: String="",state: String="",country: String="",description: String="",
               referencenumber: String="",url: String="",date: String="",category: String="",department: String="",
               groupId:Option[String],publisherId:Option[String] )
