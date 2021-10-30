package mycorda.app.ses

import mycorda.app.rss.JsonSerialiser
import java.io.File

/**
 *
 */
class FileEventStore(private val rootDirectory: String = ".") : EventStore {
    private val simpleEventStore = SimpleEventStore()
    private val rss = JsonSerialiser()
    private var eventCount = 0

    init {
        File(rootDirectory).mkdirs()
    }

    override fun read(query: EventQuery): List<Event> {
        return simpleEventStore.read(query)
    }

    override fun store(events: List<Event>): EventWriter {
        simpleEventStore.store(events)
        events.forEach {
            eventCount++
            val fileName =
                eventCount.toString().padStart(5, '0') + "-event.json"

            File("${rootDirectory}/$fileName").writeText(toJson(it))
        }
        return this
    }

    override fun storeWithChecks(events: List<Event>) {
        TODO("Not yet implemented")
    }

    private fun toJson(ev: Event): String {
        val payload = if (ev.payload != null) rss.serialiseData(ev.payload) else null
        val serializeable = SerializableEvent(
            id = ev.id.toString(),
            type = ev.type,
            aggregateId = ev.aggregateId,
            payloadAsJson = payload,
            creator = ev.creator,
            timestamp = ev.timestamp
        )

        return rss.serialiseData(serializeable)
    }

    data class SerializableEvent(
        val id: String,
        val type: String,
        val aggregateId: String?,
        val payloadAsJson: String?,
        val creator: String?,
        val timestamp: Long
    )
}

