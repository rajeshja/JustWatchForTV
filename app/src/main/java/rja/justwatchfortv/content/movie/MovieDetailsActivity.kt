package rja.justwatchfortv.content.movie

import rja.justwatchfortv.R
import rja.justwatchfortv.content.ContentDetailsActivity

class MovieDetailsActivity: ContentDetailsActivity() {
    override fun getDetailsActivity(): Int {
        return R.layout.movie_activity_details
    }
}