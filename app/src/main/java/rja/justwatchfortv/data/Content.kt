package rja.justwatchfortv.data

data class Content(val id: Int,
                   val title: String,
                   val type: String,
                   val path: String,
                   val poster: String,
                   val providerId: Int,
                   val provider: String) {}

