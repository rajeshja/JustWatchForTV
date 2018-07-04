package rja.justwatchfortv.content

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
import rja.justwatchfortv.content.movie.Movie
import rja.justwatchfortv.content.tvshow.TVShow

abstract class ContentDetailsFragment: DetailsSupportFragment() {

    private var selectedContent: BaseContent? = null
    private lateinit var selectedMovieDetails: BaseContentDetails

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

        selectedContent = activity?.intent?.getSerializableExtra(ContentDetailsActivity.CONTENT) as BaseContent
        if (selectedContent != null) {
            presenterSelector = ClassPresenterSelector()
            adapter = ArrayObjectAdapter(presenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            setupRelatedMovieListRow()
            setAdapter(adapter)
        } else {

        }
    }

    private fun setupDetailsOverviewRow() {

        doAsync {
            val details = when {
                selectedContent is Movie -> JustWatchAdapter().movieDetails(selectedContent?.id ?: 0)
                selectedContent is TVShow -> JustWatchAdapter().tvShowDetails(selectedContent?.id ?: 0)
                else -> BaseContentDetails(0, "No title", "No description", emptyArray(), emptyMap())
            }
            uiThread {
                selectedMovieDetails = details
                val row = DetailsOverviewRow(details)
                row.imageDrawable = resources.getDrawable(getBackgroundColor(), activity?.theme)
                val width = convertDpToPixel(resources, DETAIL_THUMB_WIDTH)
                val height = convertDpToPixel(resources, DETAIL_THUMB_HEIGHT)
                Glide.with(activity)
                        .load("${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${selectedContent?.poster}")
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
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(ContentDetailsDescriptionPresenter())
        detailsPresenter.backgroundColor = resources.getColor(getBackgroundColor(), activity?.theme)

        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(activity, ContentDetailsActivity.SHARED_ELEMENT_NAME)
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
    private fun initializeBackground(content: BaseContentDetails) {
        if (content.backdrops.isNotEmpty()) {
            detailsBackground.enableParallax()
            Log.d("ContentDetailsFragment",
                  "Background image URL is ${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${content.backdrops[0]}")
            Glide.with(context)
                    .load("${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${content.backdrops[0]}")
                    .asBitmap()
                    .error(R.drawable.default_background)
                    .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                            Log.d("ContentDetailsFragment", "Setting the background bitmap")
                            detailsBackground.coverBitmap = bitmap
                            adapter.notifyArrayItemRangeChanged(0, adapter.size())
                        }
                    })
        }
    }

    private fun convertDpToPixel(resources: Resources, dp: Int): Int {
        val density = resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    abstract fun getBackgroundColor() : Int
}