package attinasi.com.thanks

import org.junit.Assert.*

import org.junit.Test

class ThanksDataUnitTest {

    @Test
    @Throws(Exception::class)
    fun createThanksData() {
        val thanks = ThanksData("title", "text")
        assertEquals(thanks.text, "text")
        assertEquals(thanks.title, "title")
        assertNull(thanks.thanksImage)
    }
}