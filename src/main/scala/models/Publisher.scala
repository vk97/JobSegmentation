package models

case class Publisher(id: String, isActive: Boolean, outboundFileName: String,jobs:List[Job])
