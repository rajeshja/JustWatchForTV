package rja.justwatchfortv.content

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import rja.justwatchfortv.R

class ContentDetailsActivity: FragmentActivity() {

    companion object {
        const val CONTENT = "Content"
        const val SHARED_ELEMENT_NAME = "hero"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_activity_details)
    }
}