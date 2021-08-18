package cn.leo.compose_list.ui.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.leo.compose_list.bean.NewsBean
import cn.leo.compose_list.bean.TitleBean
import cn.leo.compose_list.model.NewsViewModel
import cn.leo.compose_list.ui.route.NewsDestinations
import cn.leo.compose_list.ui.widget.ItemNews
import cn.leo.compose_list.ui.widget.ItemTitle
import cn.leo.compose_list.ui.widget.RefreshList

@Composable
fun NewsList(navController: NavHostController) {
    val model = viewModel(modelClass = NewsViewModel::class.java)
    val lazyPagingItems = model.pager.getData().collectAsLazyPagingItems()
    RefreshList(lazyPagingItems) {
        items(lazyPagingItems) { item ->
            if (item is NewsBean.StoriesBean) {
                ItemNews(item.getTitleText(), item.getImage()) {
                    //点击跳转
                    navController.navigate(
                        "${NewsDestinations.NEWS_ROUTE}/${item.url}"
                    ) {
                        restoreState = true
                    }
                }
            } else if (item is TitleBean) {
                ItemTitle(item.title)
            }
        }
    }
}