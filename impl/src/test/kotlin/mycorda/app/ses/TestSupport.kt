package mycorda.app.ses

object SimpleEventOneFactory : EventFactory {
    fun create(aggregateId: String = "order1"): Event = Event(type = "SimpleEventOne", aggregateId = aggregateId)
}

object SimpleEventTwoFactory : EventFactory {
    fun create(aggregateId: String = "order1"): Event = Event(type = "SimpleEventTwo", aggregateId = aggregateId)
}

