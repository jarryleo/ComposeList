package cn.leo.compose_list.ui.widget

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> RefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    listState: LazyListState = rememberLazyListState(),
    index: Int = 0,
    offset: Int = 0,
    itemContent: LazyListScope.() -> Unit
) {
    val rememberSwipeRefreshState = rememberSwipeRefreshState(NORMAL)
    //错误页
    val err = lazyPagingItems.loadState.refresh is LoadState.Error
    if (err) {
        ErrorContent { lazyPagingItems.retry() }
        return
    }
    //刷新状态
    rememberSwipeRefreshState.loadState =
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> REFRESHING
            else -> NORMAL
        }
    EasySwipeRefresh(
        state = rememberSwipeRefreshState,
        onRefresh = { lazyPagingItems.refresh() },
        onLoadMore = { lazyPagingItems.retry() }
    ) {
        //列表
        LazyColumn(
            modifier = it,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            //条目布局
            itemContent()
        }
        //恢复滑动位置，有bug,分页加载可能无法恢复
        LaunchedEffect("listState") {
            Log.e("listState", "index =$index , offset = $offset")
            listState.scrollToItem(index, offset)
        }
    }
}