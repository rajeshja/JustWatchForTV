package rja.justwatchfortv.movie

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import rja.justwatchfortv.R

class MovieDetailsActivity: FragmentActivity() {

    companion object {
        const val MOVIE = "Movie"
        const val SHARED_ELEMENT_NAME = "hero"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_activity_details)
    }
}