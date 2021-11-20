package mycorda.app.ses

import mycorda.app.clock.PlatformTimer
import java.lang.RuntimeException


interface EventWriter {
    fun store(events: List<Event>): EventWriter

    fun storeWithChecks(events: List<Event>)

    fun store(event: Event): EventWriter {
        return store(listOf(event))
    }
}


interface EventReader {
    fun read(query: EventQuery): List<Event>

    /**
     * A simple polling that will block until at least
     * one event matching the query is found.
     *
     * Note that as this this implementation actively polls it
     * should be used with care in production like applications
     */
    fun pollForEvent(
        query: EventQuery,
        delayInTicks: Int = 5,
        timeoutMs: Long = 10000
    ) {
        val cutOff = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < cutOff) {
            if (this.read(query).isNotEmpty()) return
            PlatformTimer.sleepForTicks(delayInTicks)
        }
        throw ESException("Timed out waiting for event")
    }
}

interface EventStore : EventReader, EventWriter

// build up a list of specific exceptions
sealed class ESExceptions(message: String) : RuntimeException(message)
class ESLockingException(message: String) : ESExceptions(message)
class ESException(message: String) : ESExceptions(message)


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