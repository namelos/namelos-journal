package io.namelos.journal.impl

import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import io.namelos.journal.api.JournalService

class JournalServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends JournalService {
  def counter(id: String) = ServiceCall { _ =>
    val ref = persistentEntityRegistry.refFor[JournalEntity](id)
    ref.ask(GetState)
  }

  def add(id: String) = ServiceCall { n =>
    val ref = persistentEntityRegistry.refFor[JournalEntity](id)
    ref.ask(Add(n))
  }
}

