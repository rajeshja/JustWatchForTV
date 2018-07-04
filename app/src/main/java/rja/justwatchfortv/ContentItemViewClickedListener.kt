package rja.justwatchfortv

import android.app.ActivityOptions
import android.content.Intent
import android.support.v17.leanback.widget.*
import android.support.v4.app.FragmentActivity
import rja.justwatchfortv.content.Content
import rja.justwatchfortv.movie.Movie
import rja.justwatchfortv.movie.MovieDetailsActivity

class ContentItemViewClickedListener(val activity: FragmentActivity?) : OnItemViewClickedListener {
    override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any?,
                               rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
        if (item is Content) {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.CONTENT, item)
            val bundle = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME).toBundle()
            activity?.startActivity(intent, bundle)
        } else if (item is Movie) {
            val intent = Intent(activity, MovieDetailsActivity::class.java)
            intent.putExtra(MovieDetailsActivity.MOVIE, item)
            val bundle = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    MovieDetailsActivity.SHARED_ELEMENT_NAME).toBundle()
            activity?.startActivity(intent, bundle)
        }
    }
}
