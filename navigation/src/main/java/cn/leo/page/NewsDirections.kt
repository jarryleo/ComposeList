package cn.leo.page

import androidx.navigation.compose.NamedNavArgument
import cn.leo.navigation.NavigationCommand

object NewsDirections {

    val NewsList = object : NavigationCommand {

        override val arguments = emptyList<NamedNavArgument>()

        override val destination = "NewsList"

    }

    val NewsDetails = object : NavigationCommand {

        override val arguments = emptyList<NamedNavArgument>()

        override val destination = "NewsDetails"

    }
}