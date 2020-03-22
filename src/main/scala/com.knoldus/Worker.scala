package com.knoldus

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source


class Worker extends Actor with ActorLogging {

  var noOfErrors: Int = 0
  var noOfWarnings: Int = 0
  var noOfInfo: Int = 0

  def receive: Receive = {

    // case name => log.info(s"path is  $self.path")
    case file => val filePointer = Source.fromFile(s"$file")
      val listOfLines = filePointer.getLines.toList
      val totalLogFind: ActorDataStructure = tagFinder(listOfLines)
      futureWrapper(totalLogFind).pipeTo(context.sender())
      log.info(s"$context.self")
  }


  def tagFinder(listOfLines: List[String]): ActorDataStructure = {
    listOfLines match {
      case Nil => ActorDataStructure(noOfErrors, noOfWarnings, noOfInfo)
      case head :: rest if head.contains("[ERROR]") => noOfErrors += 1; tagFinder(rest)
      case head :: rest if head.contains("[WARN]") => noOfWarnings += 1; tagFinder(rest)
      case head :: rest if head.contains("[INFO]") => noOfInfo += 1; tagFinder(rest)
      case _ :: rest => tagFinder(rest)
    }
  }

  def futureWrapper(totalLogFind: ActorDataStructure): Future[ActorDataStructure] = {
    Future {
      totalLogFind
    }
  }

}