package cn.leo.page

import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import cn.leo.navigation.NavigationCommand

object NewsDirections {

    val NewsList = object : NavigationCommand {

        override val arguments = emptyList<NamedNavArgument>()

        override val destination = "NewsList"

    }

    val NewsDetails = object : NavigationCommand {

        override val arguments = listOf(navArgument("url") {
            type = NavType.StringType
            defaultValue = "https://www.baidu.com/"
        })

        override val destination = "NewsDetails"

    }
}