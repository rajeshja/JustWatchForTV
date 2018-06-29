package rja.justwatchfortv

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v17.leanback.app.BackgroundManager
import android.support.v17.leanback.app.BrowseSupportFragment
import android.support.v17.leanback.widget.*
import android.support.v4.app.ActivityOptionsCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import rja.justwatchfortv.data.BaseContent
import rja.justwatchfortv.data.Content
import rja.justwatchfortv.data.MovieList
import rja.justwatchfortv.movie.Movie
import rja.justwatchfortv.movie.MovieDetailsActivity
import java.util.*
import java.util.concurrent.ExecutionException

class HomeFragment: BrowseSupportFragment() {

    private val handler = Handler()
    private lateinit var backgroundManager: BackgroundManager
    private var defaultBackground: Drawable? = null
    private lateinit var metrics: DisplayMetrics
    private var backgroundURL: String? = null
    private var backgroundTimer: Timer? = null

    companion object {
        private const val TAG = "HomeFragment"
        private const val NUM_ROWS = 2
        private const val BACKGROUND_UPDATE_DELAY: Long = 300
        private const val GRID_ITEM_WIDTH = 200
        private const val GRID_ITEM_HEIGHT = 200
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prepareBackgroundManager()

        setupUIElements()

        loadRows()

        setupEventListeners()

    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundTimer?.cancel()
    }

    private fun prepareBackgroundManager() {
        backgroundManager = BackgroundManager.getInstance(activity)
        backgroundManager.attach(activity?.window)

        defaultBackground = resources.getDrawable(R.drawable.default_background, activity?.theme)
        metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
    }

    private fun setupUIElements() {
        badgeDrawable = resources.getDrawable(R.drawable.justwatch_icon, activity?.theme)
        title = "Search for stuff to watch using JustWatch"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        brandColor = resources.getColor(R.color.fastlane_background, activity?.theme)
        searchAffordanceColor = resources.getColor(R.color.search_opaque, activity?.theme)
    }

    private fun loadRows() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        val presenter = ContentPresenter()

        for (i in 0 until NUM_ROWS) {

            val adapter = JustWatchAdapter()
            var list: List<BaseContent>
            try {
                list = adapter.execute(MovieList.MOVIE_CATEGORY[i]).get() as List<BaseContent>
            } catch (e: InterruptedException) {
                list = ArrayList()
                Log.d(TAG, "Call was interrupted", e)
            } catch (e: ExecutionException) {
                list = ArrayList()
                Log.d(TAG, "There was an exception during execution", e)
            }

            val listRowAdapter = ArrayObjectAdapter(presenter)
            for (j in list.indices) {
                listRowAdapter.add(list[j])
            }
            val header = HeaderItem(i.toLong(), MovieList.MOVIE_CATEGORY[i])
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }

        val gridHeader = HeaderItem(NUM_ROWS.toLong(), "Preferences")

        val mGridPresenter = GridItemPresenter()
        val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
        gridRowAdapter.add(resources.getString(R.string.grid_view))
        gridRowAdapter.add(getString(R.string.error_fragment))
        gridRowAdapter.add(resources.getString(R.string.personal_settings))
        rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))

        adapter = rowsAdapter

    }

    private inner class GridItemPresenter: Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            val textView = TextView(parent.context)
            textView.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            textView.isFocusable = true
            textView.isFocusableInTouchMode = true
            textView.setBackgroundColor(resources.getColor(R.color.default_background, activity?.theme))
            textView.setTextColor(Color.WHITE)
            textView.gravity = Gravity.CENTER
            return Presenter.ViewHolder(textView)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: ViewHolder?) {}

    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            //TODO
            Toast.makeText(context, "Search not implemented yet", Toast.LENGTH_LONG).show()
        }

        onItemViewSelectedListener = ItemViewSelectedListener()
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any?,
                                   rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item is Content) {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.CONTENT, item)
//                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        activity,
//                        (itemViewHolder.view as ImageCardView).mainImageView,
//                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle()
                activity?.startActivity(intent, null)
            } else if (item is Movie) {
                val intent = Intent(activity, MovieDetailsActivity::class.java)
                intent.putExtra(MovieDetailsActivity.MOVIE, item)
//                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        activity,
//                        (itemViewHolder.view as ImageCardView).mainImageView,
//                        MovieDetailsActivity.SHARED_ELEMENT_NAME).toBundle()
                activity?.startActivity(intent, null)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(itemViewHolder: Presenter.ViewHolder?, item: Any?,
                                    rowViewHolder: RowPresenter.ViewHolder, row: Row) {
            if (item is BaseContent) {
                backgroundURL = "${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${item.posterLarge}"
                startBackgroundTimer()
            }
        }
    }

    private fun startBackgroundTimer() {
        backgroundTimer?.cancel()
        backgroundTimer = Timer()
        backgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY)
    }

    private inner class UpdateBackgroundTask : TimerTask() {
        override fun run() {
            handler.post { updateBackground(backgroundURL) }
        }
    }

    private fun updateBackground(url: String?) {
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        Glide.with(context)
                .load(url)
                .centerCrop()
                .error(defaultBackground)
                .into<SimpleTarget<GlideDrawable>>(
                        object : SimpleTarget<GlideDrawable>(width, height) {
                            override fun onResourceReady(resource: GlideDrawable?,
                                                         glideAnimation: GlideAnimation<in GlideDrawable>?) {
                                backgroundManager.drawable = resource
                            }
                        })
        backgroundTimer?.cancel()
    }
}