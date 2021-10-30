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

        val retrieved = es.read(AllEventsQuery).single()
        assertThat(retrieved, equalTo(event))
    }

    @Test
    fun `should filter by event type`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create()).store(SimpleEventTwoFactory.create())
        assertThat(es.read(AllEventsQuery).size, equalTo(2))

        val retrieved = es.read(EventTypeQuery(eventType = "SimpleEventOne")).single()
        assertThat(retrieved.type, equalTo("SimpleEventOne"))
    }

    @Test
    fun `should filter by aggregate id`() {
        val es = SimpleEventStore()

        es.store(SimpleEventOneFactory.create(aggregateId = "order1"))
            .store(SimpleEventOneFactory.create(aggregateId = "order2"))
            .store(SimpleEventOneFactory.create(aggregateId = "order3"))
        assertThat(es.read(AllEventsQuery).size, equalTo(3))

        val retrieved = es.read(AggregateIdQuery(aggregateId = "order2")).single()
        assertThat(retrieved.aggregateId, equalTo("order2"))
    }


}