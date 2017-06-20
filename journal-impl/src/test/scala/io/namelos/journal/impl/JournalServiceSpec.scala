package io.namelos.journal.impl

import io.namelos.journal.api.JournalService
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}

class JournalServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
  val server = ServiceTest.startServer(
    ServiceTest.defaultSetup.withCassandra(true)
  ) { ctx =>
    new JournalApplication(ctx) with LocalServiceLocator
  }

  val client = server.serviceClient.implement[JournalService]

  def AfterAll() = server.stop()

  "Counter service" should {
    "return value" in {
      client.counter("1").invoke().map { value =>
        value shouldBe 0
      }
    }

    "add value" in {
      for {
        _ <- client.add("1").invoke(1)
        _ <- client.add("1").invoke(2)
        value <- client.counter("1").invoke()
      } yield {
        value shouldBe 3
      }
    }
  }
}
