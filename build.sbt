name := "CurrencyConverter"

version := "0.1"

scalaVersion := "2.13.6"

val AkkaVersion = "2.6.16"
val AkkaHttpVersion = "10.2.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.9" % Test,
  "org.scalatest" %% "scalatest-funsuite" % "3.2.9" % Test,
)
