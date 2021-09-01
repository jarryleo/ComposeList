package cn.leo.compose_list.ui.page

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import cn.leo.compose_list.model.NewsViewModel
import cn.leo.compose_list.ui.theme.ComposeListTheme
import cn.leo.compose_list.ui.widget.MyAppBar
import cn.leo.navigation.NavigationManager
import cn.leo.page.Directions
import cn.leo.page.NewsDirections

@Composable
fun HomePage(manager: NavigationManager) {
    ComposeListTheme {
        Scaffold(topBar = {
            MyAppBar(title = "知乎日报")
        }) {
            val navController = rememberNavController()
            //导航控制
            manager.commands.collectAsState().value.also { command ->
                if (command.destination.isNotEmpty()) {
                    navController.navigate(command.destination)
                }
            }
            navController.addOnDestinationChangedListener { _, _, _ ->
                manager.navigate(Directions.Default)
            }
            //model
            val viewModel: NewsViewModel = viewModel()
            val lazyPagingItems = viewModel.pager.getData().collectAsLazyPagingItems()
            NavHost(
                navController = navController,
                startDestination = NewsDirections.NewsList.destination,
                builder = {
                    composable(NewsDirections.NewsList.destination) {
                        NewsList(viewModel, lazyPagingItems)
                    }
                    composable(NewsDirections.NewsDetails.destination) {
                        WebView(viewModel)
                    }
                })
        }
    }
}