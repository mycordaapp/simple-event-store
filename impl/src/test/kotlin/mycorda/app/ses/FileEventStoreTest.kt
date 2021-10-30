package mycorda.app.ses

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import mycorda.app.helpers.random
import org.junit.jupiter.api.Test
import java.io.File


class FileEventStoreTest {

    @Test
    fun `should store and read events`() {
        val testDir = newTestDir()
        val fes = FileEventStore(testDir)

        val ev1 = SimpleEventOneFactory.create()
        val ev2 = SimpleEventOneFactory.create()
        val ev3 = SimpleEventOneFactory.create()
        val originalEvents = listOf(ev1, ev2, ev3)
        fes.store(originalEvents)

        val fes2 = FileEventStore(testDir)

        val readEvents = fes2.read(EverythingQuery)
        assertThat(readEvents, equalTo(originalEvents))

    }

    private fun newTestDir(): String {
        val dir = "../.testing/${String.random()}"
        println("Test data in: ${File(dir).canonicalPath}")
        return dir
    }
}