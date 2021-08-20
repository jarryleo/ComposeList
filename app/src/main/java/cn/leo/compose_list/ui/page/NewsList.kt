package cn.leo.compose_list.ui.page

import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.leo.compose_list.bean.NewsBean
import cn.leo.compose_list.bean.TitleBean
import cn.leo.compose_list.model.NewsViewModel
import cn.leo.compose_list.ui.widget.ItemNews
import cn.leo.compose_list.ui.widget.ItemTitle
import cn.leo.compose_list.ui.widget.RefreshList

@Composable
fun NewsList(model: NewsViewModel) {
    val lazyPagingItems = model.pager.getData().collectAsLazyPagingItems()
    RefreshList(lazyPagingItems) {
        items(lazyPagingItems) { item ->
            if (item is NewsBean.StoriesBean) {
                ItemNews(item.getTitleText(), item.getImage()) {
                    //点击跳转
                    model.navigation(item.url ?: "")
                }
            } else if (item is TitleBean) {
                ItemTitle(item.title)
            }
        }
    }
}