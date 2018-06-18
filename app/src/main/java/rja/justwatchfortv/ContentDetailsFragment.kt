package rja.justwatchfortv

import android.os.Bundle
import android.support.v17.leanback.app.DetailsFragment
import android.support.v17.leanback.app.DetailsFragmentBackgroundController
import rja.justwatchfortv.data.Content

class ContentDetailsFragment: DetailsFragment() {

    private var selectedContent: Content? = null

    private lateinit var detailsBackground: DetailsFragmentBackgroundController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailsBackground = DetailsFragmentBackgroundController(this)

        selectedContent = activity.intent.getSerializableExtra("content") as Content
    }
}