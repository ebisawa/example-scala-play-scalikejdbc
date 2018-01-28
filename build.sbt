import com.typesafe.config.ConfigFactory

name := """play-scala-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.+" % Test

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.+",
  "org.scalikejdbc" %% "scalikejdbc"                  % "3.2.+",
  "org.scalikejdbc" %% "scalikejdbc-config"           % "3.2.+",
  "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.6.0-scalikejdbc-3.2"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

// scalikejdbc
enablePlugins(ScalikejdbcPlugin)
//scalikejdbcSettings

// Flyway
val appConfig = ConfigFactory.parseFile(new File("./conf/application.conf"))

flywayUrl := appConfig.getString("db.default.url")
flywayUser := appConfig.getString("db.default.username")
flywayPassword := appConfig.getString("db.default.password")
flywayLocations := Seq("filesystem:conf/db/migrations")
