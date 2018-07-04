package rja.justwatchfortv

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v17.leanback.app.DetailsFragment
import android.support.v17.leanback.app.DetailsFragmentBackgroundController
import android.support.v17.leanback.widget.*
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import rja.justwatchfortv.content.Content

class ContentDetailsFragment: DetailsFragment() {

    private var selectedContent: Content? = null

    private lateinit var detailsBackground: DetailsFragmentBackgroundController
    private lateinit var presenterSelector: ClassPresenterSelector
    private lateinit var adapter: ArrayObjectAdapter

    companion object {
        private val DETAIL_THUMB_WIDTH = 274
        private val DETAIL_THUMB_HEIGHT = 274
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        detailsBackground = DetailsFragmentBackgroundController(this)

        selectedContent = activity.intent.getSerializableExtra(DetailsActivity.CONTENT) as Content
        if (selectedContent != null) {
            presenterSelector = ClassPresenterSelector()
            adapter = ArrayObjectAdapter(presenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            setupRelatedMovieListRow()
            setAdapter(adapter)
            initializeBackground(selectedContent)
            //onItemViewClickedListener = ItemViewClickedListener()

        } else {

        }
    }

    private fun setupDetailsOverviewRow() {
        val row = DetailsOverviewRow(selectedContent)
        row.imageDrawable = ContextCompat.getDrawable(context, R.drawable.default_background)
        val width = convertDpToPixel(activity.applicationContext, DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(activity.applicationContext, DETAIL_THUMB_HEIGHT)
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
        actionAdapter.add(Action(1, "Watch Trailer", "Free"))
        actionAdapter.add(Action(2, "Rent", "Rs 100"))
        actionAdapter.add(Action(1, "Buy", "Rs 450"))

        row.actionsAdapter = actionAdapter
        adapter.add(row)
    }

    private fun setupDetailsOverviewRowPresenter() {
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor = ContextCompat.getColor(context, R.color.selected_background)

        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(activity, DetailsActivity.SHARED_ELEMENT_NAME)
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.setOnActionClickedListener {
            if (it?.id == 1L) {
                Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
            } else if (it?.id == 2L) {
                Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
            } else if (it?.id == 3L) {
                Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Who are you, O phantom clicker?", Toast.LENGTH_LONG).show()
            }
        }
        presenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun setupRelatedMovieListRow() {
        //TODO
    }

    private fun initializeBackground(content: Content?) {
        detailsBackground.enableParallax()
        Glide.with(context)
                .load("${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${content?.posterLarge}")
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .into<SimpleTarget<Bitmap>>(object: SimpleTarget<Bitmap>() {
                    override fun onResourceReady(bitmap: Bitmap, glideAnimation: GlideAnimation<in Bitmap>?) {
                        detailsBackground.coverBitmap = bitmap
                        adapter.notifyArrayItemRangeChanged(0, adapter.size())
                    }
                })
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder?, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }
}