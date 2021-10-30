package mycorda.app.ses

import java.util.*
import kotlin.collections.HashMap


/**
 * Data class to define an Event.
 */
data class Event(
    val id: UUID = UUID.randomUUID(),
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val creator: String = "???",
    val aggregateId: String? = null,
    val sessionId : String? = null,
    val payload: Map<String,Any>? = null
) {

    /**
     *
     */
    object ModelMapper {
        fun asMap(ev: Event): Map<String, Any> {
            val map = HashMap<String, Any>()
            map["id"] = ev.id
            map["type"] = ev.type
            map["creator"] = ev.creator
            map["timestamp"] = ev.timestamp

            if (ev.aggregateId != null) map["aggregateId"] = ev.aggregateId
            if (ev.sessionId != null) map["sessionId"] = ev.sessionId
            if (ev.payload != null) map["payload"] = ev.payload
            return map
        }

//        fun fromJSON (json : JSONObject) : Event {
//            val id = UUID.fromString(json.get("id") as String)
//            val type = json.getString("type")
//            val creator = json.getString("creator")
//            val timestamp = json.get("timestamp") as Long
//            val aggregateId = if (json.has("aggregateId")) json.getString("aggregateId") else null
//            val sessionId = if (json.has("sessionId")) json.getString("sessionId") else null
//            val payload = if (json.has("payload")) JsonHelper.jsonToMap(json.getJSONObject("payload")) else null
//
//
//            return Event(id = id,
//                type = type,
//                creator = creator,
//                timestamp = timestamp,
//                aggregateId = aggregateId,
//                sessionId = sessionId,
//                payload = payload)
//        }
    }

}