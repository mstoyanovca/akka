package com.example

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior}

class PrintMyActorRefActor(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "printit" =>
        val secondRef = context.spawn(Behaviors.empty[String], name = "second-actor")
        println(s"Second: $secondRef")
        this
    }
}

object PrintMyActorRefActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new PrintMyActorRefActor(context))
}

class Main(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  override def onMessage(message: String): Behavior[String] =
    message match {
      case "start" =>
        val firstRef = context.spawn(PrintMyActorRefActor(), name = "first-actor")
        println(s"First: $firstRef")
        firstRef ! "printit"
        this
    }
}

object Main {
  def apply(): Behavior[String] = Behaviors.setup(context => new Main(context))
}

object ActorHierarchyExperiments extends App {
  val testSystem = ActorSystem(Main(), "testSystem")
  testSystem ! "start"
}
