package com.knoldus

import java.io.File
import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, Identify, PoisonPill}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp, UnreachableMember}
import akka.pattern.ask
import akka.routing.RoundRobinGroup
import akka.util.Timeout
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


class Supervisor extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)
  var workerActorCollector: mutable.Set[String] = collection.mutable.Set[String]()


  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember]
    )
  }

  override def receive: Receive = {
    case MemberUp(member) =>
      val newMemberAdd = member.address.toString
      val actorRef = context.actorSelection(s"$newMemberAdd/user/master")
      actorRef ! Identify(42)
    case ActorIdentity(42, Some(master)) => workerActorCollector.add(master.path.toString);

    case DirectoryPath(path) =>
      val fileList: List[String] = fileFinder(path)
      self ! FileList(fileList, List())

    case FileList(fileList, futureList) =>

      val superRouter: ActorRef = context.actorOf(RoundRobinGroup(workerActorCollector.toList).props())
      implicit val timeout: Timeout = Timeout(5 second)
      fileList match {
        case Nil =>
          val futureResult = Future.sequence(futureList).map(_.foldLeft(ActorDataStructure(0, 0, 0)) { (acc, ele) => futureResultFinder(acc, ele) })
          futureResult.map(res => log.info(s"$res"))
        case first :: second =>
          val futureLogfound = (superRouter ? first).mapTo[ActorDataStructure]
          superRouter ! PoisonPill
          self ! FileList(second, futureLogfound :: futureList)
      }
  }

  def futureResultFinder(accumelator: ActorDataStructure, element: ActorDataStructure): ActorDataStructure = {
    ActorDataStructure(accumelator.countError + element.countError, accumelator.countWarnings
      + element.countWarnings, accumelator.countInfo + element.countInfo)
  }

  def fileFinder(path: String): List[String] = {
    val filePointer = new File(path)
    filePointer.listFiles.toList.map(_.toString)
  }

}
