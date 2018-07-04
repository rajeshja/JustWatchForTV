package rja.justwatchfortv

import android.graphics.drawable.Drawable
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import rja.justwatchfortv.content.BaseContent
import rja.justwatchfortv.content.Content
import rja.justwatchfortv.movie.Movie

class ContentPresenter : Presenter() {

    private val CARD_WIDTH = JustWatchAdapter.POSTER_WIDTH * 7 / 5
    private val CARD_HEIGHT = JustWatchAdapter.POSTER_HEIGHT * 7 / 5

    private var defaultCardImage: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {

        defaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.movie)

        val card = ImageCardView(parent.context)
        card.isFocusable = true
        card.isFocusableInTouchMode = true

        card.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            //val infoField = view.findViewById<View>(R.id.info_field)
            val contentField = view.findViewById<TextView>(R.id.content_text)
            val titleField = view.findViewById<TextView>(R.id.title_text)
            //val mainImage = view.findViewById<ImageView>(R.id.main_image).drawable

            if (hasFocus) {
                titleField.maxLines = 3
                val infoLayout = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                titleField.layoutParams = infoLayout
                val contentLayout = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                contentLayout.addRule(RelativeLayout.BELOW, R.id.title_text)
                contentField.layoutParams = contentLayout
            } else {
                titleField.maxLines = 1
            }

        }

        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val content = item as BaseContent
        val card = viewHolder.view as ImageCardView

        card.titleText = "${content.title} (${content.releaseYear})"
        if (content is Content) {
            card.contentText = content.type
        } else if (content is Movie) {
            card.contentText = "A movie"
        } else {
            card.contentText = "Got something that is neither movie nor content"
        }
        card.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        if (JustWatchAdapter.providerIcons.containsKey(content.providerId)) {
            Log.d("ContentPresenter", "content provider id = ${content.providerId}")
            card.badgeImage = ContextCompat.getDrawable(card.context, JustWatchAdapter.providerIcons[content.providerId] ?: 0)
        }
        Glide.with(viewHolder.view.context)
                .load("${JustWatchAdapter.JUSTWATCH_IMAGE_DOMAIN}${content.poster}")
                .centerCrop()
                .error(defaultCardImage)
                .into(card.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val card = viewHolder.view as ImageCardView
        card.badgeImage = null
        card.mainImage = null
    }

}