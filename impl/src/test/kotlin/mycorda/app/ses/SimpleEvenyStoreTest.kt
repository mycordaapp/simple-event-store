package mycorda.app.ses

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class SimpleEventStoreTest {

    @Test
    fun `should store and read an event`() {
        val es = SimpleEventStore()

        val event = SimpleEventOneFactory.create()
        es.store(event)

        val retrieved = es.read(EverythingQuery).single()
        assertThat(retrieved, equalTo(event))
    }

    @Test
    fun `should filter by event type`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create())
            .store(SimpleEventTwoFactory.create())
        assertThat(es.read(EverythingQuery).size, equalTo(2))

        val retrieved = es.read(EventTypeQuery(eventType = "SimpleEventOne")).single()
        assertThat(retrieved.type, equalTo("SimpleEventOne"))
    }

    @Test
    fun `should filter by like event type`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create())
            .store(FooEventFactory.create())
            .store(SimpleEventTwoFactory.create())
        assertThat(es.read(EverythingQuery).size, equalTo(3))

        val simpleEventsQuery = LikeEventTypeQuery(eventType = LikeString("SimpleEvent___"))
        val allEventsQuery = LikeEventTypeQuery(eventType = LikeString("%Event%"))

        assertThat(
            es.read(simpleEventsQuery).map { it.type },
            equalTo(listOf("SimpleEventOne", "SimpleEventTwo"))
        )
        assertThat(
            es.read(allEventsQuery).map { it.type },
            equalTo(listOf("SimpleEventOne", "FooEvent", "SimpleEventTwo"))
        )
    }

    @Test
    fun `should filter by aggregate id`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create(aggregateId = "order1"))
            .store(SimpleEventOneFactory.create(aggregateId = "order2"))
            .store(SimpleEventOneFactory.create(aggregateId = "order3"))
            .store(FooEventFactory.create(aggregateId = "fooey"))
        assertThat(es.read(EverythingQuery).size, equalTo(4))

        val retrieved = es.read(AggregateIdQuery(aggregateId = "order2")).single()
        assertThat(retrieved.aggregateId, equalTo("order2"))
    }

    @Test
    fun `should filter by multiple criteria`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create(aggregateId = "order1"))
            .store(SimpleEventOneFactory.create(aggregateId = "order2"))
            .store(SimpleEventTwoFactory.create(aggregateId = "order2"))
            .store(SimpleEventTwoFactory.create(aggregateId = "order3"))
        assertThat(es.read(EverythingQuery).size, equalTo(4))

        val aggregateIdQuery = AggregateIdQuery(aggregateId = "order2")
        val typeQuery = EventTypeQuery(eventType = "SimpleEventTwo")

        val retrieved = es.read(AllOfQuery(listOf(aggregateIdQuery, typeQuery))).single()
        assertThat(retrieved.aggregateId, equalTo("order2"))
        assertThat(retrieved.type, equalTo("SimpleEventTwo"))
    }

    @Test
    fun `should show all query examples for docs`() {
        val es = SimpleEventStore()

        val ev1 = SimpleEventOneFactory.create(aggregateId = "order1")
        val ev2 = SimpleEventOneFactory.create(aggregateId = "order2")
        val ev3 = SimpleEventTwoFactory.create(aggregateId = "order2")
        val ev4 = SimpleEventOneFactory.create(aggregateId = "order3")
        val ev5 = FooEventFactory.create(aggregateId = "fooey")
        es.store(listOf(ev1, ev2, ev3, ev4, ev5))
        assertThat(es.read(EverythingQuery).size, equalTo(5))

        val order2 = es.read(AggregateIdQuery(aggregateId = "order2"))
        assertThat(order2, equalTo(listOf(ev2, ev3)))

        val simpleEventOne = es.read(EventTypeQuery(eventType = "SimpleEventOne"))
        assertThat(simpleEventOne, equalTo(listOf(ev1, ev2, ev4)))

        val likeSimpleEvent = es.read(LikeEventTypeQuery(eventType = LikeString("SimpleEvent%")))
        assertThat(likeSimpleEvent, equalTo(listOf(ev1, ev2, ev3, ev4)))

        val compoundQuery = es.read(
            AllOfQuery(
                listOf(
                    EventTypeQuery(eventType = "SimpleEventOne"),
                    AggregateIdQuery(aggregateId = "order2")
                )
            )
        )
        assertThat(compoundQuery, equalTo(listOf(ev2)))

        val afterEv3 = es.read(LastEventIdQuery(ev3.id))
        assertThat(afterEv3, equalTo(listOf(ev4, ev5)))
    }

    @Test
    fun `should filter by last event id`() {
        val es = SimpleEventStore()

        val ev1 = SimpleEventOneFactory.create()
        val ev2 = SimpleEventOneFactory.create()
        val ev3 = SimpleEventOneFactory.create()
        es.store(listOf(ev1, ev2, ev3))
        assertThat(es.read(EverythingQuery).size, equalTo(3))

        val retrieved = es.read(LastEventIdQuery(ev2.id)).single()
        assertThat(retrieved, equalTo(ev3))
    }

}