package rja.justwatchfortv

import com.beust.klaxon.JsonObject
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName

internal class JustWatchAdapterTest {

    @Test
    fun `new Content List`() {
        val adapter = JustWatchAdapter()
        val response = adapter.newContentList()
        assert(response.size > 0)
    }

    @Test
    fun `popular Content List`() {
        val adapter = JustWatchAdapter()
        val response = adapter.popularContentList()
        assert(response.size > 0)
    }

    @Test
    fun `search for a movie`() {
        val adapter = JustWatchAdapter()
        val response = adapter.search("guardians of the galaxy")
        assert(response.size > 0)
        assert(response.size <= 30)
        assert(response[0].title == "Guardians of the Galaxy")
    }
}