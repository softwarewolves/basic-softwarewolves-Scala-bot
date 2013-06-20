organization := "softwarewolves.org"

name := "basic softwarewolves Scala bot"

version := "0.0.1"

scalaVersion := "2.9.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Jenkins-CI" at "http://repo.jenkins-ci.org/repo"

resolvers += "Sonatype OSS" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.9.1"
//  ,"org.mockito" % "mockito-core" % "1.8.5"
  ) map { _ % "test" }

libraryDependencies ++= Seq("actor") map { "com.typesafe.akka" % "akka-%s".format(_) % "2.0.5" }

libraryDependencies ++= Seq("testkit") map { "com.typesafe.akka" % "akka-%s".format(_) % "2.0.5" % "test" }

libraryDependencies ++= Seq("smack", "smackx") map { "jivesoftware" % _ % "3.2.0" }


//initialCommands := """
//import akka.actor._
//import akka.actor.Actor._
//"""

//* vim: set filetype=scala : */
