package rja.justwatchfortv

import android.app.ActivityOptions
import android.content.Intent
import android.support.v17.leanback.widget.*
import android.support.v4.app.FragmentActivity
import rja.justwatchfortv.content.BaseContent
import rja.justwatchfortv.content.Content
import rja.justwatchfortv.content.ContentDetailsActivity
import rja.justwatchfortv.content.movie.Movie
import rja.justwatchfortv.content.movie.MovieDetailsActivity
import rja.justwatchfortv.content.tvshow.TVShow
import rja.justwatchfortv.content.tvshow.TVShowDetailsActivity

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
            startDetailsActivity(item, MovieDetailsActivity::class.java, itemViewHolder)
        } else if (item is TVShow) {
            startDetailsActivity(item, TVShowDetailsActivity::class.java, itemViewHolder)
        }
    }

    private fun startDetailsActivity(item: BaseContent, clazz: Class<*>, itemViewHolder: Presenter.ViewHolder) {
        val intent = Intent(activity, clazz)
        intent.putExtra(ContentDetailsActivity.CONTENT, item)
        val bundle = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                (itemViewHolder.view as ImageCardView).mainImageView,
                ContentDetailsActivity.SHARED_ELEMENT_NAME).toBundle()
        activity?.startActivity(intent, bundle)
    }
}
