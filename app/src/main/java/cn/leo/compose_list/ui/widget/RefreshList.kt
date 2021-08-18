package cn.leo.compose_list.ui.widget

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun <T : Any> RefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: LazyListScope.() -> Unit
) {
    val rememberSwipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    //错误页
    val err = lazyPagingItems.loadState.refresh is LoadState.Error
    if (err) {
        ErrorContent { lazyPagingItems.retry() }
        return
    }
    SwipeRefresh(
        state = rememberSwipeRefreshState,
        onRefresh = { lazyPagingItems.refresh() }) {
        //刷新状态
        rememberSwipeRefreshState.isRefreshing =
            lazyPagingItems.loadState.refresh is LoadState.Loading
        //列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //条目布局
            itemContent()
            //加载更多状态：加载中和加载错误,没有更多
            item {
                lazyPagingItems.apply {
                    when (loadState.append) {
                        is LoadState.Loading -> LoadingItem()
                        is LoadState.Error -> ErrorItem { retry() }
                        is LoadState.NotLoading -> NoMoreItem()
                    }
                }
            }
        }
    }
}