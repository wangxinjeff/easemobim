package com.hyphenate.easemob.im.officeautomation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.view.KeyEvent
import android.webkit.WebViewClient
import android.view.ViewGroup
import android.view.KeyEvent.KEYCODE_BACK
import android.webkit.WebChromeClient
import com.hyphenate.easemob.R
import android.os.Message
import android.view.View
import android.widget.ProgressBar

class WebActivity : BaseActivity() {

    private lateinit var webView: WebView
    private lateinit var back: ImageView
    private lateinit var title: TextView
    private lateinit var ivClose: ImageView
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        webView = `$`(R.id.webview)
        back = `$`(R.id.iv_back)
        title = `$`(R.id.tv_web_title)
        ivClose = `$`(R.id.iv_close)
        progressBar = `$`(R.id.progressBar)

        back.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }

        ivClose.setOnClickListener {
            finish()
        }
        val titleName = intent.getStringExtra("title")
        title.text = titleName

        val url = intent.getStringExtra("url")

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.loadsImagesAutomatically = true

        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        webSettings.domStorageEnabled = true

        //缩放操作
        webSettings.setSupportZoom(true)

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        // 5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }


        webSettings.setSupportMultipleWindows(false)
        webSettings.javaScriptCanOpenWindowsAutomatically = true


        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
                println("progress:$newProgress")
            }

            override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = view
                resultMsg.sendToTarget()
                return true
            }
        }
        if (url != null) {
            webView.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.clearHistory()
        (webView.parent as ViewGroup).removeView(webView)
        webView.loadUrl("about:blank")
        webView.stopLoading()
        webView.webChromeClient = null
        webView.destroy()
    }
}