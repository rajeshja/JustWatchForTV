package rja.justwatchfortv.movie

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v17.leanback.app.DetailsSupportFragment
import android.support.v17.leanback.app.DetailsSupportFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rja.justwatchfortv.JustWatchAdapter
import rja.justwatchfortv.R

class MovieDetailsFragment: DetailsSupportFragment() {

    private var selectedMovie: Movie? = null
    private lateinit var selectedMovieDetails: MovieDetails

    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var adapter: ArrayObjectAdapter

    companion object {
        private const val DETAIL_THUMB_WIDTH = 274
        private const val DETAIL_THUMB_HEIGHT = 274
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        detailsBackground = DetailsSupportFragmentBackgroundController(this)

        selectedMovie = activity?.intent?.getSerializableExtra(MovieDetailsActivity.MOVIE) as Movie
        if (selectedMovie != null) {
            presenterSelector = ClassPresenterSelector()
            adapter = ArrayObjectAdapter(presenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            setupRelatedMovieListRow()
            setAdapter(adapter)
            //onItemViewClickedListener = ItemViewClickedListener()

        } else {

        }
    }

    private fun setupDetailsOverviewRow() {

        doAsync {
            val details = JustWatchAdapter().movieDetails(selectedMovie?.id ?: 0)
            uiThread {
                selectedMovieDetails = details
                val row = DetailsOverviewRow(details)
                row.imageDrawable = resources.getDrawable(R.drawable.default_background, activity?.theme)
                val width = convertDpToPixel(resources, DETAIL_THUMB_WIDTH)
                val height = convertDpToPixel(resources, DETAIL_THUMB_HEIGHT)
                Glide.with(activity)
                        .load("${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${selectedMovie?.poster}")
                        .centerCrop()
                        .error(R.drawable.default_background)
                        .into(object: SimpleTarget<GlideDrawable>(width, height) {
                            override fun onResourceReady(resource: GlideDrawable?, glideAnimation: GlideAnimation<in GlideDrawable>?) {
                                row.imageDrawable = resource
                                adapter.notifyArrayItemRangeChanged(0, adapter.size())
                            }
                        })

                val actionAdapter = ArrayObjectAdapter()
                for (offer in selectedMovieDetails.offers) {
                    actionAdapter.add(Action(offer.value.id.toLong(), offer.key, ""))
                }

                row.actionsAdapter = actionAdapter
                adapter.add(row)
                adapter.add(row)

                initializeBackground(details)
            }
        }
    }

    private fun setupDetailsOverviewRowPresenter() {
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(MovieDetailsDescriptionPresenter())
        detailsPresenter.backgroundColor = resources.getColor(R.color.movie_background, activity?.theme)

        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(activity, MovieDetailsActivity.SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.setOnActionClickedListener {
            val movieIntent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedMovieDetails.offers[it?.label1]?.url))
            try {
                startActivity(movieIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "Oops. Couldn't find an app to open this link: " + movieIntent.dataString,
                        Toast.LENGTH_LONG).show()
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun setupRelatedMovieListRow() {
        //TODO
    }

    //This code should be common for both Movie and TV Shows
    private fun initializeBackground(movie: MovieDetails) {
        if (movie.backdrops.isNotEmpty()) {
            detailsBackground.enableParallax()
            Log.d("MovieDetailsFragment",
                  "Background image URL is ${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${movie.backdrops[0]}")
            Glide.with(context)
                    .load("${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${movie.backdrops[0]}")
                    .asBitmap()
                    .error(R.drawable.default_background)
                    .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                            Log.d("MovieDetailsFragment", "Setting the background bitmap")
                            detailsBackground.coverBitmap = bitmap
                            adapter.notifyArrayItemRangeChanged(0, adapter.size())
                        }
                    })
        }
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private fun convertDpToPixel(resources: Resources, dp: Int): Int {
        val density = resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}