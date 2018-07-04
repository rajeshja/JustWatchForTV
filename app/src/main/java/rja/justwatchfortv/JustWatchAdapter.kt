package rja.justwatchfortv

import android.os.AsyncTask
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import rja.justwatchfortv.content.BaseContent
import rja.justwatchfortv.content.BaseContentDetails
import rja.justwatchfortv.content.Content
import rja.justwatchfortv.content.StreamingDetails
import rja.justwatchfortv.movie.Movie
import rja.justwatchfortv.movie.MovieDetails
import rja.justwatchfortv.tvshow.TVShowDetails
import java.net.URL
import java.net.URLEncoder

class JustWatchAdapter : AsyncTask<String, Void, List<BaseContent>>() {

    companion object {
        const val POSTER_WIDTH = 166
        const val POSTER_HEIGHT = 243
        const val JUSTWATCH_IMAGE_DOMAIN = "https://images.justwatch.com"
        const val SEARCH_RESULT_SIZE = 30
        const val SEARCH_TYPEAHEAD_SIZE = 5

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
                11 to "Mubi",
                158 to "Viu",
                0 to "Unknown Provider"
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
                11 to R.drawable.mubi,
                158 to R.drawable.viu
        )

        val MOVIE_CATEGORY = arrayOf(
                "New",
                "Popular")

        const val newQueryFirstPage: String = "https://apis.justwatch.com/content/titles/en_IN/new?body={\"monetization_types\":[\"flatrate\",\"free\",\"ads\",\"rent\",\"buy\"],\"page\":1,\"page_size\":3,\"titles_per_provider\":6}"
        const val popularQueryFirstPage: String = "https://apis.justwatch.com/content/titles/en_IN/popular?body={\"monetization_types\":[\"flatrate\",\"free\",\"ads\",\"buy\",\"rent\"],\"page\":1,\"page_size\":15}"
        const val movieDetailsQuery: String = "https://apis.justwatch.com/content/titles/movie/%s/locale/en_IN"
        const val tvShowDetailsQuery: String = "https://apis.justwatch.com/content/titles/show/%s/locale/en_IN"
        const val searchContentQuery: String = "https://apis.justwatch.com/content/titles/en_IN/popular?body={\"content_types\":[\"show\",\"movie\"],\"page\":1,\"page_size\":%d,\"query\":\"%s\"}"
    }

    override fun doInBackground(vararg category: String): List<BaseContent> {
        return listByCategory(category[0])
    }

    private fun listByCategory(category: String): List<BaseContent> {
        return when (category) {
            "New" -> newContentList()
            "Popular" -> popularContentList()
            else -> emptyList()
        }
    }

    fun newContentList(): List<BaseContent> {
        val contents = ArrayList<BaseContent>()
        val result = queryAsJson(newQueryFirstPage)
        val days = result.array<JsonObject>("days")?: JsonArray()
        for(day in days) {
            val providers = day.array<JsonObject>("providers")?: JsonArray()
            for(provider in providers) {
                val items = provider.array<JsonObject>("items")?: JsonArray()
                for(item in items) {
                    contents.add(itemToContent(item, provider))
                }
            }
        }
        return contents
    }

    fun popularContentList(): List<BaseContent> {
        val contents = ArrayList<BaseContent>()
        val result = queryAsJson(popularQueryFirstPage)
        val items = result.array<JsonObject>("items") ?: JsonArray()
        for(item in items) {
            contents.add(itemToContent(item, null))
        }
        return contents
    }

    private fun itemToContent(item: JsonObject, provider: JsonObject?): BaseContent {
        val code = (item["full_path"] as String).split("/").lastOrNull()
        val origPoster = item["poster"] as String? ?: ""
        val title = createTitle(item)
        val posterPrefix = (origPoster).replace("{profile}", "")
        if (item["object_type"] == "movie") {
            return Movie(
                    id = item["id"] as Int,
                    title = title,
                    path = item["full_path"] as String? ?: "/no/path",
                    poster = "${posterPrefix}s166/$code",
                    posterLarge = "${posterPrefix}s592/$code",
                    releaseYear = item["original_release_year"] as Int? ?: 0,
                    providerId = provider?.get("provider_id") as Int? ?: 0,
                    provider = providers[provider?.get("provider_id") as Int? ?: 0] ?: "",
                    availableQuality = emptyArray()
            )
        } else {
            return Content(
                    id = item["id"] as Int,
                    title = title,
                    type = item["object_type"] as String? ?: "No Type",
                    path = item["full_path"] as String? ?: "/no/path",
                    poster = "${posterPrefix}s166/$code",
                    posterLarge = "${posterPrefix}s592/$code",
                    releaseYear = item["original_release_year"] as Int? ?: 0,
                    providerId = provider?.get("provider_id") as Int? ?: 0,
                    provider = providers[provider?.get("provider_id") as Int? ?: 0] ?: ""
            )
        }
    }

    private fun createTitle(item: JsonObject): String {
        return if (item["object_type"] == "show_season") {
            "${item["show_title"]} - ${item["title"] ?: item["original_title"]}"
        } else {
            item["title"] as String? ?: "No Title"
        }
    }

    fun tvShowDetails(id: Int): TVShowDetails {
        val detail = queryAsJson(tvShowDetailsQuery.format(id))
        return itemToContentDetails(detail) as TVShowDetails
    }

    fun movieDetails(id: Int): MovieDetails {
        val detail = queryAsJson(movieDetailsQuery.format(id))
        return itemToContentDetails(detail) as MovieDetails
    }

    private fun itemToContentDetails(detail: JsonObject): BaseContentDetails {
        val movieCode = (detail["full_path"] as String).split("/").lastOrNull()
        val backdrops = detail.array<JsonObject>("backdrops") ?: JsonArray()
        val backdropURLs = mutableListOf<String>()
        for (backdrop in backdrops) {
            val url = backdrop["backdrop_url"] as String?
            if (url != null) {
                val urlPrefix = url.replace("{profile}", "")
                backdropURLs.add("${urlPrefix}s1440/$movieCode")
            }
        }
        val offers = mutableMapOf<String, StreamingDetails>()
        val offerOptions = detail.array<JsonObject>("offers") ?: JsonArray()
        for (offer in offerOptions) {
            val providerId = offer["provider_id"] as Int
            val provider = providers[providerId] ?: ""
            val urls = (offer["urls"] as JsonObject? ?: JsonObject())
            val url = urls.get(key = "deeplink_android") as String? ?: urls["standard_web"] as String
            offers[provider] = StreamingDetails(providerId, url)
        }
        return MovieDetails(id = detail["id"] as Int,
                title = detail["title"] as String? ?: detail["original_title"] as String? ?: "No Title",
                description = detail["short_description"] as String? ?: "No Description",
                backdrops = backdropURLs.toTypedArray(),
                offers = offers)
    }

    fun searchAhead(query: String): List<BaseContent> {
        return search(query, SEARCH_TYPEAHEAD_SIZE)
    }

    fun search(query: String): List<BaseContent> {
        return search(query, SEARCH_RESULT_SIZE)
    }

    private fun search(query: String, resultSize: Int): List<BaseContent> {
        val contents = ArrayList<BaseContent>()
        val result = queryAsJson(searchContentQuery.format(resultSize, URLEncoder.encode(query, "utf-8")))
        val items = result.array<JsonObject>("items") ?: JsonArray()
        for(item in items) {
            contents.add(itemToContent(item, null))
        }
        return contents
    }

    private fun queryAsJson(query: String): JsonObject {
        return URL(query).openStream().let {
            Parser().parse(it) as JsonObject
        }
    }
}
