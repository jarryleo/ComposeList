package cn.leo.compose_list.ui.page

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.leo.compose_list.bean.NewsBean
import cn.leo.compose_list.bean.TitleBean
import cn.leo.compose_list.model.NewsViewModel
import cn.leo.compose_list.ui.widget.ItemNews
import cn.leo.compose_list.ui.widget.ItemTitle
import cn.leo.compose_list.ui.widget.RefreshList

@Composable
fun NewsList() {
    val model = viewModel(modelClass = NewsViewModel::class.java)
    val lazyPagingItems = model.pager.getData().collectAsLazyPagingItems()
    RefreshList(lazyPagingItems) {
        items(lazyPagingItems) { item ->
            if (item is NewsBean.StoriesBean) {
                val context = LocalContext.current
                ItemNews(item.getTitleText(), item.getImage()) {
                    //点击跳转
                    ContextCompat.startActivity(
                        context,
                        Intent(Intent.ACTION_VIEW, Uri.parse(item.url)),
                        null
                    )
                }
            } else if (item is TitleBean) {
                ItemTitle(item.title)
            }
        }
    }
}