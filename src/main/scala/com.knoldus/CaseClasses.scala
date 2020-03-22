package com.knoldus

import scala.concurrent.Future


case class DirectoryPath(path:String)
case class ActorDataStructure(countError: Int, countWarnings: Int, countInfo: Int)
case class FileList(fileList:List[String],futureList: List[Future[ActorDataStructure]])


