package mycorda.app.ses

import mycorda.app.helpers.random
import java.lang.RuntimeException
import java.util.*

enum class Colour {
    Red, Green, Blue;

    companion object {
        fun random(): Colour = Colour.values()[Random().nextInt(2)]
    }
}

data class DemoModel(
    val string: String = String.random(80),
    val int: Int = Random().nextInt(),
    val long: Long = Random().nextLong(),
    val double: Double = Random().nextDouble(),
    val float: Float = Random().nextFloat(),
    val boolean: Boolean = Random().nextBoolean(),
    val colour: Colour = Colour.random(),
    val nested: DemoModel? = null
)

class DemoException(message: String) : RuntimeException(message)


object SimpleEventOneFactory : EventFactory {
    fun create(aggregateId: String = "order1"): Event = Event(type = "SimpleEventOne", aggregateId = aggregateId)
}

object SimpleEventTwoFactory : EventFactory {
    fun create(aggregateId: String = "order1"): Event = Event(type = "SimpleEventTwo", aggregateId = aggregateId)
}