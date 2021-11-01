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

/*
 For wildcard searching. Uses SQL LIKE rules

 Standard (ANSI) SQL has two wildcard characters for use with the LIKE keyword:
    _ (underscore). Matches a single occurrence of any single character.
    % (percent sign). Matches zero or more occurrences of any single character.

@TODO - this should be a common type
 */

data class LikeString(val like: String, val escape: Char = '!') {

    private val regex = like.replace("%", ".{0,}")
        .replace("_", ".").toRegex()

    fun toRegex(): Regex = regex

}

/**
 * Common queries
 */
data class AggregateIdQuery(val aggregateId: String) : EventQuery()
data class EventTypeQuery(val eventType: String) : EventQuery()
data class LikeEventTypeQuery(val eventType: LikeString) : EventQuery()
data class LastEventIdQuery(override val lastEventId: EventId) : LastEventId, EventQuery()

object EverythingQuery : EventQuery()


// All queries must match
class AllOfQuery(private val queries: List<EventQuery>) : Iterable<EventQuery>, EventQuery() {
    override fun iterator(): Iterator<EventQuery> = queries.listIterator()
}