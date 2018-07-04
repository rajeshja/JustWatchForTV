package rja.justwatchfortv.content.tvshow

import rja.justwatchfortv.R
import rja.justwatchfortv.content.ContentDetailsActivity

class TVShowDetailsActivity: ContentDetailsActivity() {
    override fun getDetailsActivity(): Int {
        return R.layout.tvshow_activity_details
    }
}