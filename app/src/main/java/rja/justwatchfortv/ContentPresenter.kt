package rja.justwatchfortv

import android.graphics.drawable.Drawable
import android.support.v17.leanback.widget.ImageCardView
import android.support.v17.leanback.widget.Presenter
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.bumptech.glide.Glide
import rja.justwatchfortv.data.Content

class ContentPresenter() : Presenter() {

    private val CARD_WIDTH = JustWatchAdapter.POSTER_WIDTH * 3 / 2
    private val CARD_HEIGHT = JustWatchAdapter.POSTER_HEIGHT * 3 / 2

    val JUSTWATCH_IMAGE_DOMAIN = "https://images.justwatch.com"

    private var defaultCardImage: Drawable? = null;

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {

        defaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.movie)

        val card = ImageCardView(parent.context)
        card.isFocusable = true
        card.isFocusableInTouchMode = true

        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val content = item as Content
        val card = viewHolder.view as ImageCardView

        card.titleText = content.title
        card.contentText = "${content.type}"
        card.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        if (content.providerId != 0) {
            card.badgeImage = ContextCompat.getDrawable(card.context, JustWatchAdapter.providerIcons[content.providerId]
                    ?: 0)
        }
        Glide.with(viewHolder.view.context)
                .load("${JUSTWATCH_IMAGE_DOMAIN}${content.poster}")
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