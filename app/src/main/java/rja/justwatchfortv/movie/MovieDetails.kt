package rja.justwatchfortv.movie

import android.graphics.drawable.Drawable

data class MovieDetails(val id: Int,
                        val title: String,
                        val description: String,
                        val backdrops: Array<String>,
                        val offers: Map<String, StreamingDetails>)

data class StreamingDetails(val id: Int,
                            val url: String)