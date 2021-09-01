package cn.leo.compose_list.ui.page

import android.R
import android.annotation.SuppressLint
import android.net.http.SslError
import android.util.Log
import android.webkit.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cn.leo.compose_list.model.NewsViewModel

@Composable
fun WebView(model: NewsViewModel) {
    val url = model.url
    val showError = remember { mutableStateOf(false) }
    if (showError.value) {
        WebErrorContent {
            showError.value = false
        }
        return
    }
    Box {
        val progress = remember { mutableStateOf(0f) }
        val showProgress = remember { mutableStateOf(true) }
        AndroidView(factory = { WebView(it) }) { web ->
            web.apply {
                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        if (request?.isForMainFrame == true) {
                            showError.value = true
                        }
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        if (request?.isForMainFrame == true) {
                            showError.value = true
                        }
                    }

                    @SuppressLint("WebViewClientOnReceivedSslError")
                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        handler?.proceed()
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        progress.value = newProgress.toFloat() / 100f
                        showProgress.value = newProgress != 100
                    }
                }
            }
            web.settings.apply {
                javaScriptEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                useWideViewPort = true
                domStorageEnabled = true
                builtInZoomControls = true
                loadWithOverviewMode = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportZoom(false)
                setSupportMultipleWindows(true)
                setGeolocationEnabled(true)
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            }
            web.loadUrl(url)
            Log.e("WebView", "加载网址：$url")
        }
        if (showProgress.value) {
            LinearProgressIndicator(
                progress = progress.value,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WebErrorContent(retry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(
                painter = painterResource(id = R.drawable.stat_notify_error),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Red),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "请求出错啦",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
            )
            Button(
                onClick = { retry() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            ) {
                Text(text = "重试")
            }
        }
    }
}
