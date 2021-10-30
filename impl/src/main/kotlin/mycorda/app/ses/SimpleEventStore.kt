package mycorda.app.ses

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * The most simple EventStore - everything is in memory
 */
class SimpleEventStore(initialCapacity: Int = 10) : EventStore {
    private val events: MutableList<Event> = ArrayList(initialCapacity)
    private val eventIdLookup: MutableMap<EventId, Int> = HashMap(initialCapacity)
    override fun read(query: EventQuery): List<Event> {
        return this.events.filter { checkFilter(it, query) }
    }

    override fun store(events: List<Event>): EventWriter {
        synchronized(this) {
            var index = this.events.size
            events.forEach {
                this.events.add(it)
                eventIdLookup[it.id] = index
                index++
            }
        }
        return this
    }

    override fun storeWithChecks(events: List<Event>) {
        TODO("Not yet implemented")
    }

    private fun checkFilter(ev: Event, query: EventQuery): Boolean {
        return when (query) {
            is AggregateIdQuery -> (query.aggregateId == ev.aggregateId)
            is EventTypeQuery -> (query.eventType == ev.type)
            is LastEventQuery -> true
            is AllEventsQuery -> true
            is AllQueries -> {
                // the rule is all
                var matched = true
                query.forEach {
                    matched = matched && checkFilter(ev, it)
                }
                matched
            }
        }
    }


}