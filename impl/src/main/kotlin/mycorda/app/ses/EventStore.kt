package mycorda.app.ses

import java.lang.RuntimeException
import java.util.*


interface EventWriter {
    fun store(events: List<Event>): EventWriter

    fun storeWithChecks(events: List<Event>)

    fun store(event: Event): EventWriter {
        return store(listOf(event))
    }
}


interface EventReader {
    fun read(query: EventQuery): List<Event>
}

interface EventStore : EventReader, EventWriter

// build up a list of specific exceptions
sealed class ESException(message: String) : RuntimeException(message)
class ESLockingException(message: String) : ESException(message)
//class ESLockingException(message : String) : ESException(message)


/**
 * In many cases consumers have already consumed events, and simply
 * just want any new ones
 */
interface LastEventId {
    val lastEventId: EventId?
}

// all the possible queries
sealed class EventQuery

/**
 * Common queries
 */
data class AggregateIdQuery(val aggregateId: String, override val lastEventId: EventId? = null) : LastEventId, EventQuery()

data class EventTypeQuery(val eventType: String, override val lastEventId: EventId? = null) : LastEventId, EventQuery()

data class LastEventQuery(override val lastEventId: EventId) : LastEventId, EventQuery()

object AllEventsQuery : EventQuery()


// All queries must match
class AllQueries(private val queries: List<EventQuery>) : Iterable<EventQuery>, EventQuery() {
    override fun iterator(): Iterator<EventQuery> = queries.listIterator()
}