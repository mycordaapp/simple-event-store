package mycorda.app.ses

object SimpleEventOneFactory : EventFactory {
    fun create(aggregateId: String = "order1"): Event = Event(type = "SimpleEventOne", aggregateId = aggregateId)
}

object SimpleEventTwoFactory : EventFactory {
    fun create(aggregateId: String = "order1"): Event = Event(type = "SimpleEventTwo", aggregateId = aggregateId)
}

data class Foo(val value: String = "foobar")
object FooEventFactory : EventFactory {
    fun create(aggregateId: String = "order1", payload: Foo = Foo()): Event =
        Event(type = "FooEvent", aggregateId = aggregateId, payload = payload)
}


