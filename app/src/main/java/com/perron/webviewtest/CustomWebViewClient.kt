package com.perron.webviewtest

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

class CustomWebViewClient: WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val canScrollHorizontally = view?.let {
            it.canScrollHorizontally(1) || it.canScrollHorizontally(-1)
        } ?: false
        val canScrollVertically = view?.let {
            it.canScrollVertically(1) || it.canScrollVertically(-1)
        } ?: false
        val canScrollVerticallyManually = view?.let {
            it.contentHeight > it.height
        } ?: false

        Log.d("CustomWebViewClient", "Page finished loading ($url).\n" +
                "Can scroll horizontally: $canScrollHorizontally\n" +
                "Can scroll vertically: $canScrollVertically\n" +
                "Can scroll vertically manually: $canScrollVerticallyManually\n" +
                "content height: ${view?.contentHeight}, view height: ${view?.height}\n" +
                "scroll extent: ${(view as? NestedScrollableWebview)?.extent}, scroll range: ${(view as? NestedScrollableWebview)?.range}")
        (view as? NestedScrollableWebview)?.scrollable = canScrollHorizontally || canScrollVertically
    }
}