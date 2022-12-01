package com.example

import akka.actor.typed._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

class SupervisingActor(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  private val child = context.spawn(
    Behaviors.supervise(SupervisedActor()).onFailure(SupervisorStrategy.restart),
    name = "supervised-actor")

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "failChild" =>
        child ! "fail"
        this
    }
}

object SupervisingActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new SupervisingActor(context))
}

class SupervisedActor(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("supervised actor started")

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "fail" =>
        println("supervised actor fails now")
        throw new Exception("I failed!")
    }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PreRestart =>
      println("supervised actor will be restarted")
      this
    case PostStop =>
      println("supervised actor stopped")
      this
  }
}

object SupervisedActor {
  def apply(): Behavior[String] = Behaviors.setup(context => new SupervisedActor(context))
}

class FailureHandlingExampleMain(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  override def onMessage(message: String): Behavior[String] =
    message match {
      case "start" =>
        val supervisingActor = context.spawn(SupervisingActor(), "supervising-actor")
        supervisingActor ! "failChild"
        this
    }
}

object FailureHandlingExampleMain {
  def apply(): Behavior[String] = Behaviors.setup(context => new FailureHandlingExampleMain(context))
}

object FailureHandlingExample extends App {
  val testSystem = ActorSystem(FailureHandlingExampleMain(), "testSystem")
  testSystem ! "start"
}
