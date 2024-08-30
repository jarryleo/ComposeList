package cn.leo.navigation

import cn.leo.page.Directions.Default
import kotlinx.coroutines.flow.MutableStateFlow

object NavigationManager {

    var commands = MutableStateFlow(Default)

    fun navigate(directions: NavigationCommand) {
        commands.value = directions
    }

}