package cn.leo.compose_list.ui.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun MyAppBar(title: String) {
    TopAppBar(
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text = title)
    }
}