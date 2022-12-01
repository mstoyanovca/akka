package com.example

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, PostStop, Signal}

class Actor1(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("first started")
  context.spawn(Actor2(), "second")

  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "stop" => Behaviors.stopped
    }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("first stopped")
      this
  }
}

object Actor1 {
  def apply(): Behavior[String] = Behaviors.setup(context => new Actor1(context))
}

class Actor2(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  println("second started")

  override def onMessage(msg: String): Behavior[String] = {
    Behaviors.unhandled
  }

  override def onSignal: PartialFunction[Signal, Behavior[String]] = {
    case PostStop =>
      println("second stopped")
      this
  }
}

object Actor2 {
  def apply(): Behavior[String] = Behaviors.setup(new Actor2(_))
}

class ActorStopExampleMain(context: ActorContext[String]) extends AbstractBehavior[String](context) {
  override def onMessage(message: String): Behavior[String] =
    message match {
      case "start" =>
        val first = context.spawn(Actor1(), name = "first")
        first ! "stop"
        this
    }
}

object ActorStopExampleMain {
  def apply(): Behavior[String] = Behaviors.setup(context => new ActorStopExampleMain(context))
}

object ActorStopExample extends App {
  val testSystem = ActorSystem(ActorStopExampleMain(), "testSystem")
  testSystem ! "start"
}
