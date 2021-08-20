package cn.leo.compose_list.ui.page

import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                Log.e("HomePage: ", "commands")
                if (command.destination.isNotEmpty()) {
                    navController.navigate(command.destination)
                }
            }
            navController.addOnDestinationChangedListener { _, _, _ ->
                manager.navigate(Directions.Default)
            }
            NavHost(
                navController = navController,
                startDestination = NewsDirections.NewsList.destination,
                builder = {
                    composable(route = NewsDirections.NewsList.destination) {
                        NewsList(
                            hiltViewModel(
                                navController.getBackStackEntry(
                                    route = NewsDirections.NewsList.destination
                                )
                            )
                        )
                    }
                    composable(
                        route = NewsDirections.NewsDetails.destination,
                        arguments = NewsDirections.NewsDetails.arguments
                    ) { backStackEntry ->
                        val url = backStackEntry.arguments?.getString("url")
                        if (url != null) {
                            WebView(url)
                        }
                    }
                })
        }
    }
}