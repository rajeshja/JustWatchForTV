package rja.justwatchfortv.movie

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter
import android.util.Log
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rja.justwatchfortv.JustWatchAdapter

class MovieDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder, item: Any) {
        val movie = item as MovieDetails

        viewHolder.title.text = movie.title
        viewHolder.body.text = "..."
        viewHolder.body.text = movie.description
    }
}
