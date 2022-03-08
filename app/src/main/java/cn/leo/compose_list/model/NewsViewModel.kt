package cn.leo.compose_list.model

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import cn.leo.compose_list.bean.TitleBean
import cn.leo.compose_list.paging.SimplePager
import cn.leo.navigation.NavigationManager
import cn.leo.page.NewsDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : BaseViewModel() {

    private val mDate = Calendar.getInstance().apply {
        add(Calendar.DATE, 1)
    }

    private val initialKey = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        .format(mDate.time)
        .toLong()

    val pager = SimplePager<Long, Any>(
        viewModelScope,
        enablePlaceholders = true
    ) {
        val date = it.key ?: initialKey
        try {
            //从网络获取数据
            val data = api.getNews(date)
            //添加title
            val list: MutableList<Any> = data.stories.toMutableList()
            list.add(0, TitleBean(date.toString()))
            //返回数据
            PagingSource.LoadResult.Page(list, null, data.date?.toLongOrNull())
        } catch (e: Exception) {
            //请求失败
            PagingSource.LoadResult.Error(e)
        }
    }

    var url: String = ""
    fun navigation(url: String) {
        this.url = url
        navigationManager.navigate(NewsDirections.NewsDetails)
    }

    var firstVisibleItemIndex = 0
    var firstVisibleItemScrollOffset = 0
}