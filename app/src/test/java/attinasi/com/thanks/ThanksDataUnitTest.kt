package attinasi.com.thanks

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.security.InvalidParameterException

class ThanksDataUnitTest {

    @Before
    fun initialize() {
        ThanksDataStore.instance.reset()
    }

    @Test
    @Throws(Exception::class)
    fun createThanksData() {
        val thanks = ThanksData("title", "text")
        assertEquals(thanks.text, "text")
        assertEquals(thanks.title, "title")
        assertNull(thanks.thanksImage)
    }

    @Test
    @Throws(Exception::class)
    fun addemptyThanksData() {
        val thanksStore = ThanksDataStore.instance
        assert(thanksStore.count() == 0)
    }

    @Test
    @Throws(Exception::class)
    fun addThanks() {
        val thanks = ThanksData("title", "text")
        val thanksStore = ThanksDataStore.instance
        thanksStore.add(thanks)
        assert(thanksStore.count() == 1)
        assertEquals(thanksStore.get(0), thanks)
    }

    @Test
    @Throws(Exception::class)
    fun removeThanks() {
        val thanks = ThanksData("title", "text")
        val thanksStore = ThanksDataStore.instance

        // throws if nothing to remove
        try {
            thanksStore.remove(thanks)
            assert(false)
        } catch (ex: InvalidParameterException) {
            assert(true)
        }

        // throws if trying to remove something not in the store
        thanksStore.add(thanks)
        try {
            thanksStore.remove(ThanksData("foo", "bar"))
            assert(false)
        } catch (ex: InvalidParameterException) {
            assert(true)
        }

        // success
        thanksStore.remove(thanks)
        assert(thanksStore.count() == 0)
    }

    @Test
    @Throws(Exception::class)
    fun restoreThanks() {
        val thanks = ThanksData("title", "text")
        val thanksStore = ThanksDataStore.instance
        thanksStore.add(thanks)
        thanksStore.remove(thanks)
        assert(thanksStore.count() == 0)
        thanksStore.restore(thanks)
        assert(thanksStore.count() == 1)
        assertEquals(thanksStore.get(0), thanks)

        try {
            thanksStore.restore(thanks)
            assertEquals("Exception expected", null)
        } catch (ex: InvalidParameterException) {
            assertEquals(true, true)
        }
    }
}