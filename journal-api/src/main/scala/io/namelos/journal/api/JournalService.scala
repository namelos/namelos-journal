package io.namelos.journal.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}

trait JournalService extends Service {
  import Service._

  def counter(id: String): ServiceCall[NotUsed, Int]

  def add(id: String): ServiceCall[Int, Done]

  def descriptor = {
    named("journal")
      .withCalls(
        pathCall("/api/counter/:id", counter _),
        pathCall("/api/counter/:id", add _)
      )
      .withAutoAcl(true)
  }
}

