package cn.leo.compose_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cn.leo.compose_list.ui.page.HomePage
import cn.leo.navigation.NavigationManager

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomePage(NavigationManager)
        }
    }
}

