package io.namelos.journal.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import io.namelos.journal.api.JournalService
import play.api.db.HikariCPComponents
//import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.persistence.jdbc.JdbcPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

class JournalLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new JournalApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new JournalApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[JournalService]
  )
}

abstract class JournalApplication(context: LagomApplicationContext) extends LagomApplication(context)
  with JdbcPersistenceComponents
  with HikariCPComponents
  with LagomKafkaComponents
  with AhcWSComponents {
  lazy val lagomServer = serverFor[JournalService](wire[JournalServiceImpl])

  lazy val jsonSerializerRegistry = JournalSerializerRegistry

  persistentEntityRegistry.register(wire[JournalEntity])
}

