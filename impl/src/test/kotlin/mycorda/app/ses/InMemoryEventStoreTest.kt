package mycorda.app.ses



class InMemoryEventStoreTest : BaseEventStoreTest() {
    override fun createEventStore(): EventStore = InMemoryEventStore()

}