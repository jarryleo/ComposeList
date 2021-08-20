package cn.leo.page

import androidx.navigation.compose.NamedNavArgument
import cn.leo.navigation.NavigationCommand

object Directions {

    val Default = object : NavigationCommand {

        override val arguments = emptyList<NamedNavArgument>()

        override val destination = ""

    }
}