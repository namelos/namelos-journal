package io.namelos.journal.impl

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.Json

import scala.collection.immutable.Seq

class JournalEntity extends PersistentEntity {
  type Command = JournalCommand[_]
  type Event = JournalEvent
  type State = JournalState

  def initialState = JournalState(0)

  def behavior = {
    case JournalState(_) => Actions()
      .onCommand[Add, Done] { case (Add(x), ctx, _) => ctx.thenPersist(Added(x)) { _ => ctx.reply(Done) } }
      .onReadOnlyCommand[GetState.type, Int] { case (GetState, ctx, JournalState(n)) => ctx.reply(n) }
      .onEvent { case (Added(x), JournalState(n)) => JournalState(n + x) }
  }
}

case class JournalState(n: Int)
object JournalState { implicit val format = Json.format[JournalState] }

sealed trait JournalEvent extends AggregateEvent[JournalEvent] { def aggregateTag = JournalEvent.Tag }
object JournalEvent { val Tag = AggregateEventTag[JournalEvent] }
case class Added(n: Int) extends JournalEvent
object Added { implicit val format = Json.format[Added] }

sealed trait JournalCommand[R] extends ReplyType[R]
case class Add(n: Int) extends JournalCommand[Done]
object Add { implicit val format = Json.format[Add] }
case object GetState extends JournalCommand[Int] { implicit val format = JsonSerializer.emptySingletonFormat(GetState) }

object JournalSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Add],
    JsonSerializer[Added],
    JsonSerializer[JournalState],
    JsonSerializer[GetState.type]
  )
}

