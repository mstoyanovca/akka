package com.example

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorSystem, Behavior, PostStop, Signal}

class IotSupervisor(context: ActorContext[Nothing]) extends AbstractBehavior[Nothing](context) {
  context.log.info("IoT Application started")

  override def onMessage(msg: Nothing): Behavior[Nothing] = Behaviors.unhandled

  override def onSignal: PartialFunction[Signal, Behavior[Nothing]] = {
    case PostStop =>
      context.log.info("IoT Application stopped")
      this
  }
}

object IotSupervisor {
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing](context => new IotSupervisor(context))
}

object IotApp {
  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](IotSupervisor(), "iot-system")
  }
}
