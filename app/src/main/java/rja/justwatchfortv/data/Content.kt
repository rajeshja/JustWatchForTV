package rja.justwatchfortv.data

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
        BaseContent(id, title, path, poster, posterLarge, releaseYear, providerId, provider),
        Serializable

open class BaseContent(open val id: Int,
                       open val title: String,
                       open val path: String,
                       open val poster: String,
                       open val posterLarge: String,
                       open val releaseYear: Int,
                       open val providerId: Int,
                       open val provider: String) : Serializable