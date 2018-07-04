package rja.justwatchfortv.content

import java.io.Serializable

data class Content(override val id: Int,
                   override val title: String,
                   val type: String,
                   override val path: String,
                   override val poster: String,
                   override val posterLarge: String,
                   override val releaseYear: Int,
                   override val providerId: Int,
                   override val provider: String):
        BaseContent(id, title, path, poster, posterLarge, releaseYear, providerId, provider, emptyArray(), ""),
        Serializable

open class BaseContent(open val id: Int,
                       open val title: String,
                       open val path: String,
                       open val poster: String,
                       open val posterLarge: String,
                       open val releaseYear: Int,
                       open val providerId: Int,
                       open val provider: String,
                       open val availableQuality: Array<String>,
                       open var description: String? = null,
                       open var backdrops: Array<String> = emptyArray()) : Serializable

open class BaseContentDetails(open val id: Int,
                              open val title: String,
                              open val description: String,
                              open val backdrops: Array<String>,
                              open val offers: Map<String, StreamingDetails>) : Serializable

data class StreamingDetails(val id: Int,
                            val url: String)