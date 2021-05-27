package cn.leo.compose_list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.leo.compose_list.bean.NewsBean
import cn.leo.compose_list.bean.TitleBean
import cn.leo.compose_list.model.NewsViewModel
import cn.leo.compose_list.paging.SimplePager
import cn.leo.compose_list.ui.theme.ComposeListTheme
import cn.leo.compose_list.widget.ItemNews
import cn.leo.compose_list.widget.ItemTitle
import cn.leo.compose_list.widget.RefreshList

class MainActivity : ComponentActivity() {

    private val model by viewModels<NewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeListTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    NewsList(pager = model.pager)
                }
            }
        }
    }
}

@Composable
fun NewsList(pager: SimplePager<Long, Any>) {
    val lazyPagingItems = pager.getData().collectAsLazyPagingItems()
    RefreshList(lazyPagingItems) {
        items(lazyPagingItems) { item ->
            if (item is NewsBean.StoriesBean) {
                val context = LocalContext.current
                ItemNews(item.getTitleText(), item.getImage()) {
                    //点击跳转
                    startActivity(
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
