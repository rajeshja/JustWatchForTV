package rja.justwatchfortv.content.tvshow

import rja.justwatchfortv.content.BaseContentDetails
import rja.justwatchfortv.content.StreamingDetails

data class TVShowDetails(override val id: Int,
                    override val title: String,
                    override val description: String,
                    override val backdrops: Array<String>,
                    override val offers: Map<String, StreamingDetails>):
        BaseContentDetails(id, title, description, backdrops, offers)