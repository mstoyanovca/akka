#!/usr/bin/env bash

val AkkaVersion = "2.7.0"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion

./sbt-dist/bin/sbt "$@"
