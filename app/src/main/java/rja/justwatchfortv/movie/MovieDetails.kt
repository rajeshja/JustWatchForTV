package rja.justwatchfortv.movie

import rja.justwatchfortv.content.BaseContentDetails
import rja.justwatchfortv.content.StreamingDetails

data class MovieDetails(override val id: Int,
                        override val title: String,
                        override val description: String,
                        override val backdrops: Array<String>,
                        override val offers: Map<String, StreamingDetails>) :
        BaseContentDetails(id, title, description, backdrops, offers)

