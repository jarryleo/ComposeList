package cn.leo.compose_list.bean

import androidx.annotation.Keep

@Keep
data class NewsBean(
    var date: String? = "",
    var stories: List<StoriesBean> = emptyList()
) {
    @Keep
    data class StoriesBean(
        var type: Int = 0,
        var id: Int = 0,
        var ga_prefix: String? = null,
        var title: String? = null,
        var url: String? = null,
        var images: List<String>? = null
    ) {
        fun getTitleText(): String = title ?: ""
        fun getImage(): String = images?.getOrNull(0) ?: ""
    }
}
