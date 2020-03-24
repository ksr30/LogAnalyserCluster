package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.typesafe.config.ConfigFactory

object SupervisorNode extends App {

  def startCluster(ports: List[Int]): Unit = ports.foreach { port =>
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.netty.tcp.port = $port
         """.stripMargin)
      .withFallback(ConfigFactory.load("clusterErrorFinder.conf"))

    val system = ActorSystem("KSRCluster", config)
    val supervisor = system.actorOf(Props[Supervisor], "Supervisor")
    Thread.sleep(20000)
    supervisor ! DirectoryPath("./src/main/resources/LogFiles")
  }

  startCluster(List(6666))
}

object workerNode extends App {
  private val poolSize = 5

  def startCluster(ports: List[Int]): Unit = ports.foreach { port =>
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port = $port
         """.stripMargin)
      .withFallback(ConfigFactory.load("clusterErrorFinder.conf"))

    val system = ActorSystem("KSRCluster", config)
    // val worker=system.actorOf(Props[Worker], "Worker")
    val master = system.actorOf(RoundRobinPool(poolSize).props(Props[Worker]), "master")

  }

  startCluster(List(2555))
}

object workerNode1 extends App {
  private val poolSize = 5

  def startCluster(ports: List[Int]): Unit = ports.foreach { port =>
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port = $port
         """.stripMargin)
      .withFallback(ConfigFactory.load("clusterErrorFinder.conf"))

    val system = ActorSystem("KSRCluster", config)
    // val worker=system.actorOf(Props[Worker], "Worker")
    val master = system.actorOf(RoundRobinPool(poolSize).props(Props[Worker]), "master")

  }

  startCluster(List(2552))
}

object workerNode2 extends App {
  private val poolSize = 5

  def startCluster(ports: List[Int]): Unit = ports.foreach { port =>
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port = $port
         """.stripMargin)
      .withFallback(ConfigFactory.load("clusterErrorFinder.conf"))

    val system = ActorSystem("KSRCluster", config)
    // val worker=system.actorOf(Props[Worker], "Worker")
    val master = system.actorOf(RoundRobinPool(poolSize).props(Props[Worker]), "master")

  }

  startCluster(List(2553))
}

object workerNode3 extends App {
  private val poolSize = 5

  def startCluster(ports: List[Int]): Unit = ports.foreach { port =>
    val config = ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port = $port
         """.stripMargin)
      .withFallback(ConfigFactory.load("clusterErrorFinder.conf"))

    val system = ActorSystem("KSRCluster", config)
    // val worker=system.actorOf(Props[Worker], "Worker")
    val master = system.actorOf(RoundRobinPool(poolSize).props(Props[Worker]), "master")

  }

  startCluster(List(2556))
}