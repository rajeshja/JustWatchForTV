package rja.justwatchfortv

import android.os.AsyncTask
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import rja.justwatchfortv.data.Content
import java.net.URL



class JustWatchAdapter() : AsyncTask<String, Void, List<Content>>() {

    companion object {
        const val POSTER_WIDTH = 166
        const val POSTER_HEIGHT = 243
        val providers = hashMapOf(
                3 to "Google Play",
                192 to "YouTube",
                122 to "Hotstar",
                8 to "Netflix",
                175 to "Netflix Kids",
                121 to "Voot",
                100 to "GuideDoc",
                125 to "Hooq",
                73 to "Tubi TV",
                2 to "Apple iTunes",
                119 to "Amazon Prime Video",
                11 to "Mubi"
        )
        val providerIcons = hashMapOf(
                3 to R.drawable.playmovies,
                192 to R.drawable.youtube,
                122 to R.drawable.hotstar,
                8 to R.drawable.netflix,
                175 to R.drawable.netflixkids,
                121 to R.drawable.voot,
                100 to R.drawable.guidedoc,
                125 to R.drawable.hooq,
                73 to R.drawable.tubitv,
                2 to R.drawable.itunes,
                119 to R.drawable.primevideo,
                11 to R.drawable.mubi
        )

        val MOVIE_CATEGORY = arrayOf(
                "New",
                "Popular")

    }

    val newQueryFirstPage: String = "https://apis.justwatch.com/content/titles/en_IN/new?body={\"monetization_types\":[\"flatrate\",\"free\",\"ads\",\"rent\",\"buy\"],\"page\":1,\"page_size\":3,\"titles_per_provider\":6}"
    val popularQueryFirstPage: String = "https://apis.justwatch.com/content/titles/en_IN/popular?body={\"monetization_types\":[\"flatrate\",\"free\",\"ads\",\"buy\",\"rent\"],\"page\":1,\"page_size\":15}"

    override fun doInBackground(vararg category: String): List<Content> {
        return listByCategory(category[0])
    }

    fun listByCategory(category: String): List<Content> {
        if (category == "New") {
            return newContentList()
        } else if (category == "Popular") {
            return popularContentList()
        } else {
            return emptyList()
        }
    }

    fun newContentList(): List<Content> {
        var contents = ArrayList<Content>()
        val result = queryAsJson(newQueryFirstPage)
        val days: JsonArray<JsonObject> = result.array<JsonObject>("days")?: JsonArray<JsonObject>()
        for(day in days) {
            val providers = day.array<JsonObject>("providers")?: JsonArray<JsonObject>()
            for(provider in providers) {
                val items = provider.array<JsonObject>("items")?: JsonArray<JsonObject>()
                for(item in items) {
                    contents.add(itemToContent(item, provider))
                }
            }
        }
        return contents
    }

    fun popularContentList(): List<Content> {
        var contents = ArrayList<Content>()
        var result = queryAsJson(popularQueryFirstPage)
        val items = result.array<JsonObject>("items") ?: JsonArray<JsonObject>()
        for(item in items) {
            contents.add(itemToContent(item, null))
        }
        return contents
    }

    private fun itemToContent(item: JsonObject, provider: JsonObject?): Content {
        val code = (item.get("full_path") as String).split("/").lastOrNull()
        val origPoster = item.get("poster") as String? ?: ""
        val title = item.get("title") as String? ?:
            (item.get("show_title") as String?) ?:
            (item.get("original_title") as String?) ?:
            "No title"
        val posterPrefix = (origPoster).replace("{profile}", "")
         return Content(
                id = item.get("id") as Int ?: 0,
                title = title,
                type = item.get("object_type") as String? ?: "No Type",
                path = item.get("full_path") as String? ?: "/no/path",
                poster = "${posterPrefix}s166/${code}",
                providerId = provider?.get("provider_id") as Int? ?: 0,
                provider = providers[provider?.get("provider_id") as Int? ?: 0] ?: ""
        )
    }

    private fun queryAsJson(query: String): JsonObject {
        val result = URL(query).openStream().let {
            Parser().parse(it) as JsonObject
        }
        return result
    }
}
