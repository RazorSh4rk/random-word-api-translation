scalaVersion := "2.13.7"

name := "translator"
organization := "com.heroku.random-word-api"
version := "1.0"

libraryDependencies ++=  Seq(
    "org.scalaj" %% "scalaj-http" % "2.4.2",
    "com.typesafe.play" %% "play-json" % "2.8.1"
)