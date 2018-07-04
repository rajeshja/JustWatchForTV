package rja.justwatchfortv.content

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter
import rja.justwatchfortv.content.movie.MovieDetails

class ContentDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder, item: Any) {
        val movie = item as MovieDetails

        viewHolder.title.text = movie.title
        viewHolder.body.text = "..."
        viewHolder.body.text = movie.description
    }
}
