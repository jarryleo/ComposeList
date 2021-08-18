package cn.leo.compose_list.ui.page

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import cn.leo.compose_list.ui.route.NewsDestinations
import cn.leo.compose_list.ui.route.NewsDestinations.NEWS_URL_KEY
import cn.leo.compose_list.ui.theme.ComposeListTheme
import cn.leo.compose_list.ui.widget.MyAppBar
import cn.leo.compose_list.ui.widget.WebView

@Composable
fun HomePage() {
    ComposeListTheme {
        Scaffold(topBar = {
            MyAppBar(title = "知乎日报")
        }) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = NewsDestinations.NEWS_LIST_ROUTE,
                builder = {
                    composable(route = NewsDestinations.NEWS_LIST_ROUTE) {
                        NewsList(navController)
                    }
                    composable(
                        route = "${NewsDestinations.NEWS_ROUTE}/{$NEWS_URL_KEY}",
                        arguments = listOf(navArgument(NEWS_URL_KEY) {
                            type = NavType.StringType
                        })
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString(NEWS_URL_KEY)
                        if (url != null) {
                            WebView(url)
                        }
                    }
                })
        }
    }
}