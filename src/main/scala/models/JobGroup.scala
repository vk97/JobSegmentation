package models

case class JobGroup(id: String, rules: List[Rule], sponsoredPublishers: List[Publisher],jobs:List[Job])
