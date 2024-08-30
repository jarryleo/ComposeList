package cn.leo.compose_list.ui.page

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import cn.leo.compose_list.bean.NewsBean
import cn.leo.compose_list.bean.TitleBean
import cn.leo.compose_list.model.NewsViewModel
import cn.leo.compose_list.ui.widget.ItemNews
import cn.leo.compose_list.ui.widget.ItemTitle
import cn.leo.compose_list.ui.widget.RefreshList

@Composable
fun NewsList(
    model: NewsViewModel,
    lazyPagingItems: LazyPagingItems<Any>
) {
    val index = model.firstVisibleItemIndex
    val offset = model.firstVisibleItemScrollOffset
    val listState = rememberLazyListState(index, offset)
    RefreshList(lazyPagingItems, listState, index, offset) {
        items(lazyPagingItems.itemCount) { index ->
            val item = lazyPagingItems[index]
            if (item is NewsBean.StoriesBean) {
                ItemNews(item.getTitleText(), item.getImage()) {
                    //点击跳转
                    model.firstVisibleItemIndex = listState.firstVisibleItemIndex
                    model.firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset
                    model.navigation(item.url ?: "")
                }
            } else if (item is TitleBean) {
                ItemTitle(item.title)
            }
        }
    }
}