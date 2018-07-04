package rja.justwatchfortv

import java.io.Serializable

data class TVShow (val id: Int,
                   val title: String,
                   val path: String,
                   val poster: String,
                   val releaseYear: Int,
                   val providerId: Int,
                   val provider: String,
                   val offers: Map<String, OfferDetails>) : Serializable

data class OfferDetails (val monetization: String,
                         val totalEpisodes: Int,
                         val newEpisodes: Int)