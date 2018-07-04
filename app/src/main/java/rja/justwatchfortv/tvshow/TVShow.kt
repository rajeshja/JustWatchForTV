package rja.justwatchfortv.tvshow

import rja.justwatchfortv.content.BaseContent
import java.io.Serializable

data class TVShow (override val id: Int,
                   override val title: String,
                   override val path: String,
                   override val poster: String,
                   override val posterLarge: String,
                   override val releaseYear: Int,
                   override val providerId: Int,
                   override val provider: String,
                   val availableQuality: Array<String>,
                   var description: String? = null,
                   var backdrops: Array<String> = emptyArray()) :
        BaseContent(id, title, path, poster, posterLarge, releaseYear, providerId, provider),
        Serializable