package rja.justwatchfortv.content.tvshow

import rja.justwatchfortv.content.BaseContent

data class TVShow (override val id: Int,
                   override val title: String,
                   override val path: String,
                   override val poster: String,
                   override val posterLarge: String,
                   override val releaseYear: Int,
                   override val providerId: Int,
                   override val provider: String,
                   override val availableQuality: Array<String>,
                   override var description: String? = null,
                   override var backdrops: Array<String> = emptyArray()) :
        BaseContent(id, title, path, poster, posterLarge, releaseYear, providerId, provider, availableQuality, description)