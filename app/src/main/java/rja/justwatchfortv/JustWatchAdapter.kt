package rja.justwatchfortv

import android.os.AsyncTask
import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import rja.justwatchfortv.data.BaseContent
import rja.justwatchfortv.data.Content
import rja.justwatchfortv.movie.Movie
import rja.justwatchfortv.movie.MovieDetails
import rja.justwatchfortv.movie.StreamingDetails
import java.net.URL

class JustWatchAdapter : AsyncTask<String, Void, List<BaseContent>>() {

    companion object {
        const val POSTER_WIDTH = 166
        const val POSTER_HEIGHT = 243
        const val JUSTWATCH_IMAGE_DOMAIN = "https://images.justwatch.com"

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

    val movieDetailsQuery: String = "https://apis.justwatch.com/content/titles/movie/%s/locale/en_IN"

    override fun doInBackground(vararg category: String): List<BaseContent> {
        return listByCategory(category[0])
    }

    fun listByCategory(category: String): List<BaseContent> {
        if (category == "New") {
            return newContentList()
        } else if (category == "Popular") {
            return popularContentList()
        } else {
            return emptyList()
        }
    }

    fun newContentList(): List<BaseContent> {
        val contents = ArrayList<BaseContent>()
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

    fun popularContentList(): List<BaseContent> {
        val contents = ArrayList<BaseContent>()
        val result = queryAsJson(popularQueryFirstPage)
        val items = result.array<JsonObject>("items") ?: JsonArray<JsonObject>()
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
                    poster = "${posterPrefix}s166/${code}",
                    posterLarge = "${posterPrefix}s592/${code}",
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
                    poster = "${posterPrefix}s166/${code}",
                    posterLarge = "${posterPrefix}s592/${code}",
                    releaseYear = item["original_release_year"] as Int? ?: 0,
                    providerId = provider?.get("provider_id") as Int? ?: 0,
                    provider = providers[provider?.get("provider_id") as Int? ?: 0] ?: ""
            )
        }
    }

    private fun itemToTVShow(item: JsonObject, provider: JsonObject?): TVShow {
        val code = (item["full_path"] as String).split("/").lastOrNull()
        val origPoster = item["poster"] as String? ?: ""
        val title = createTitle(item)
        val posterPrefix = (origPoster).replace("{profile}", "")
        val offers
                = mutableMapOf<String, OfferDetails>()
        val offersArray = item.array<JsonObject>("offers") ?: JsonArray<JsonObject>()
        for(offer in offersArray) {
            val quality = offer["presentation_type"] as String?
            if (quality != null) {
                val monetization = offer["monetization_type"] as String? ?: ""
                val totalEpisodes = offer["element_count"] as Int? ?: 0
                val newEpisodes = offer["new_element_count"] as Int? ?: 0
                offers.put(quality, OfferDetails(monetization, totalEpisodes, newEpisodes))
            }
        }
        return TVShow(
                id = item["id"] as Int,
                title = title,
                path = item["full_path"] as String? ?: "/no/path",
                poster = "${posterPrefix}s166/${code}",
                releaseYear = item["original_release_year"] as Int? ?: 0,
                providerId = provider?.get("provider_id") as Int? ?: 0,
                provider = providers[provider?.get("provider_id") as Int? ?: 0] ?: "",
                offers = offers
        )
    }

    private fun createTitle(item: JsonObject): String {
        val title: String
        if (item.get("object_type") == "show_season") {
            title = "${item.get("show_title")} - ${item.get("title") ?: item.get("original_title")}"
        } else {
            title = item.get("title") as String? ?: "No Title"
        }
        return title
    }

    fun movieDetails(id: Int): MovieDetails {
        val detail = queryAsJson(movieDetailsQuery.format(id))
        val movieCode = (detail["full_path"] as String).split("/").lastOrNull()
        val backdrops = detail.array<JsonObject>("backdrops") ?: JsonArray()
        val backdropURLs = mutableListOf<String>()
        for(backdrop in backdrops) {
            val url = backdrop.get("backdrop_url") as String?
            if (url!=null) {
                val urlPrefix = url.replace("{profile}", "")
                backdropURLs.add("${urlPrefix}s1440/${movieCode}")
            }
        }
        val offers = mutableMapOf<String, StreamingDetails>()
        val offerOptions = detail.array<JsonObject>("offers") ?: JsonArray()
        for (offer in offerOptions) {
            val providerId = offer.get("provider_id") as Int
            val provider = providers[providerId] ?: ""
            val urls = (offer.get("urls") as JsonObject? ?: JsonObject())
            val url = urls.get("deeplink_android") as String? ?: urls.get("standard_web") as String
            offers.put(provider, StreamingDetails(providerId, url))
        }
        return MovieDetails(id = id,
                title = detail.get("title") as String? ?: detail.get("original_title") as String? ?: "No Title",
                description = detail.get("short_description") as String? ?: "No Description",
                backdrops = backdropURLs.toTypedArray(),
                offers = offers)
    }

    private fun queryAsJson(query: String): JsonObject {
        val result = URL(query).openStream().let {
            Parser().parse(it) as JsonObject
        }
        return result
    }
}
