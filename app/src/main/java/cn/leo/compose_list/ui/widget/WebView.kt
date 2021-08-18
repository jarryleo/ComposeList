package cn.leo.compose_list.ui.widget

import android.R
import android.annotation.SuppressLint
import android.net.http.SslError
import android.webkit.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(url: String) {
    var progress = remember { 0f }
    var showProgress = remember { true }
    var showError = remember { false }
    if (showError) {
        WebErrorContent {
            showError = false
        }
        return
    }
    Box {
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
                            showError = true
                        }
                    }
                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        super.onReceivedHttpError(view, request, errorResponse)
                        if (request?.isForMainFrame == true) {
                            showError = true
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
                        progress = newProgress.toFloat()
                        showProgress = newProgress != 100
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
        }
        if (showProgress) {
            LinearProgressIndicator(progress = progress)
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
