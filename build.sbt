organization in ThisBuild := "io.namelos"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.2.5" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % Test
val postgres = "org.postgresql" % "postgresql" % "9.4.1212"

lazy val `journal` = (project in file("."))
  .aggregate(`journal-api`, `journal-impl`)

lazy val `journal-api` = (project in file("journal-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `journal-impl` = (project in file("journal-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
//      lagomScaladslPersistenceCassandra,
      postgres,
      lagomScaladslPersistenceJdbc,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`journal-api`)
