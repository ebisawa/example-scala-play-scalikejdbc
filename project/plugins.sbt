addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.11")

resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.+"
addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "3.2.0")
