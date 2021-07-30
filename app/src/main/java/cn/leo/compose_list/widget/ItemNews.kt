package cn.leo.compose_list.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ItemNews(title: String, image: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .height(84.dp)
            .padding(16.dp)

    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .align(Alignment.CenterVertically),
            text = title,
            maxLines = 2
        )
        Image(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = rememberImagePainter(image),
            contentDescription = title
        )
    }
}

@Composable
fun ItemTitle(title: String) {
    Text(
        modifier = Modifier
            .background(Color.Gray)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        text = title,
        color = Color.White,
        maxLines = 1
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewList() {
    ItemNews(
        title = "测试",
        image = "https://pic4.zhimg.com/v2-cdef72deff2f2876bbe73eca7b7f12e3.jpg?source=8673f162"
    )
}