package com.perron.webviewtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.perron.webviewtest.ui.theme.WebViewTestTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebViewTestTheme {
                var mode: WebViewMode by remember { mutableStateOf(WebViewMode.STANDALONE) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "WebView Test",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.primary)
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(color = if(mode == WebViewMode.STANDALONE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                        .clickable{
                                            mode = WebViewMode.STANDALONE
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Standalone WebView",
                                        color = if(mode == WebViewMode.STANDALONE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                VerticalDivider()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if(mode == WebViewMode.NESTED) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                        .clickable{
                                            mode = WebViewMode.NESTED
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nested WebView",
                                        color = if(mode == WebViewMode.NESTED) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                    )
                                }

                                VerticalDivider()
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if(mode == WebViewMode.NESTED_NOT_SCROLLABLE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                                        .clickable{
                                            mode = WebViewMode.NESTED_NOT_SCROLLABLE
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nested Non-scroll WebView",
                                        color = if(mode == WebViewMode.NESTED_NOT_SCROLLABLE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    WebViewContent(
                        mode = mode,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun WebViewContent(mode: WebViewMode, modifier: Modifier = Modifier) {
    when(mode) {
        WebViewMode.STANDALONE -> StandaloneWebView(modifier = modifier.fillMaxHeight())
        WebViewMode.NESTED -> NestedWebView(
            url = "https://loremipsum.io/",
            modifier = modifier.fillMaxHeight()
        )
        WebViewMode.NESTED_NOT_SCROLLABLE -> NestedWebView(
            url = "invalid", //"https://www.york.ac.uk/teaching/cws/wws/webpage1.html",
            modifier = modifier.fillMaxHeight()
        )
    }
}

@Composable
private fun StandaloneWebView(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        InternalWebView(
            url = "https://loremipsum.io/",
            scrollable = false,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun NestedWebView(url: String, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        for(i in 1..14) {
            item(key = i) {
                Text(
                    text = "$i: Some content above the WebView",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        item(key = url) {
            InternalWebView(
                url = url,
                scrollable = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(horizontal = 16.dp)
            )
        }
        for(i in 15..25) {
            item(key = i) {
                Text(
                    text = "$i: Some content below the WebView",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun InternalWebView(url: String, scrollable: Boolean, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            val webView = if(scrollable) {
                NestedScrollableWebview(context)
            } else {
                WebView(context)
            }
            webView.apply {
                settings.javaScriptEnabled = true
                webViewClient = CustomWebViewClient()
            }
        },
        modifier = modifier,
        update = { webView -> webView.loadUrl(url) },
        onRelease = { webView ->
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.clearHistory()
        },
    )
}