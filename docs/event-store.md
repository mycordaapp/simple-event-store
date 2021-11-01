# The Simple Event Store

## Defining Events

An event is simply an instance of [Event](../impl/src/main/kotlin/mycorda/app/ses/Event.kt)

At a minimum an Event should have a unique `id` and a `type`, which by convention uses Java naming conventions,
e.g. `com.example.MyEvent`. In most cases there is an `aggregateId` which links all events related to an entity such as
an orderId or a customerNumber.

Most events also have custom data, the `payload`. This can be anything that meets the following criteria.

- it is serializable by [Really Simple Serialisation](https://github.com/mycordaapp/really-simple-serialisation#readme)
- the json serialized format fits within the current size limit, currently 32KB.

Finally, there are some common audit fields - currently just `author` and `timestamp`.

As events aren't in themselves type safe, the recommended approach is a single factory object for each event type. See
below for an example. Note that the pattern of pulling a suitable aggregateId out of the payload is quite common but
ultimately an implementation decision.

```kotlin
data class Customer(val id: String, val firstName: String, val lastName: String)

object CustomerCreatedEventFactory : EventFactory {
    fun create(customer: Customer): Event {
        return Event(
            type = "com.example.CustomerCreated",
            aggregateId = customer.id,
            payload = customer
        )
    }
}


```
