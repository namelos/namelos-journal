package io.namelos.journal.impl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.lightbend.lagom.scaladsl.testkit.PersistentEntityTestDriver
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class JournalEntitySpec extends WordSpec with Matchers with BeforeAndAfterAll {
  val system = ActorSystem("JournalEntitySpec",
    JsonSerializerRegistry.actorSystemSetupFor(JournalSerializerRegistry))

  override def afterAll() = TestKit.shutdownActorSystem(system)

  def withTestDriver(block: PersistentEntityTestDriver[JournalCommand[_], JournalEvent, JournalState] => Unit)= {
    val driver = new PersistentEntityTestDriver(system, new JournalEntity, "hello-1")
    block(driver)
    driver.getAllIssues should have size 0
  }

  "Hello entity" should {
    "say hello by default" in withTestDriver { driver =>
      val outcome = driver.run(GetState)
      outcome.replies should contain only 0
    }

    "allow updating the greeting message" in withTestDriver { driver =>
      val outcome1 = driver.run(Add(1))
      outcome1.events should contain only Added(1)
      val outcome2 = driver.run(GetState)
      outcome2.replies should contain only 1
    }
  }
}
