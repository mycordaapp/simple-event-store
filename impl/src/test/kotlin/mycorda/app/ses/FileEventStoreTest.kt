package mycorda.app.ses

import mycorda.app.helpers.random
import org.junit.jupiter.api.Test

class FileEventStoreTest {
    val testId = String.random()

    @Test
    fun `should do something`(){
        val fes = FileEventStore("../.testing/$testId")

        fes.store(SimpleEventOneFactory.create())
        fes.store(SimpleEventOneFactory.create())
        fes.store(SimpleEventOneFactory.create())


    }
}