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

        es.store(SimpleEventOneFactory.create()).store(SimpleEventTwoFactory.create())
        assertThat(es.read(EverythingQuery).size, equalTo(2))

        val retrieved = es.read(EventTypeQuery(eventType = "SimpleEventOne")).single()
        assertThat(retrieved.type, equalTo("SimpleEventOne"))
    }

    @Test
    fun `should filter by aggregate id`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create(aggregateId = "order1"))
            .store(SimpleEventOneFactory.create(aggregateId = "order2"))
            .store(SimpleEventOneFactory.create(aggregateId = "order3"))
        assertThat(es.read(EverythingQuery).size, equalTo(3))

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
    fun `should filter by last event id`() {
        val es = SimpleEventStore()

        val ev1 = SimpleEventOneFactory.create()
        val ev2 = SimpleEventOneFactory.create()
        val ev3 = SimpleEventOneFactory.create()
        es.store(listOf(ev1, ev2, ev3))
        assertThat(es.read(EverythingQuery).size, equalTo(3))

        val retrieved = es.read(LastEventQuery(ev2.id)).single()
        assertThat(retrieved, equalTo(ev3))
    }

}