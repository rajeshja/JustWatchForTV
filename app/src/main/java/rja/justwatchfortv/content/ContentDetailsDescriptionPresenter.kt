package rja.justwatchfortv.content

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter

class ContentDetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder, item: Any) {
        val content = item as BaseContentDetails

        viewHolder.title.text = content.title
        viewHolder.body.text = "..."
        viewHolder.body.text = content.description
    }
}
