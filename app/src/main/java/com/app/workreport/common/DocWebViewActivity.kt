package com.app.workreport.common

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.workreport.databinding.ActivityWebViewBinding
import com.app.workreport.util.FILE_PATH
import com.app.workreport.util.loadImage

class DocWebViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityWebViewBinding
    var url = ""
    var title = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleViews()
    }

    private fun handleViews() {
        binding.ilToolBar.apply {
            btnLogout.isVisible = false
            textTitle.text = "View Document"
        }



        intent?.extras?.let {
            url = it.getString(FILE_PATH) ?: ""
          //  title = it.getString(TITLE) ?: ""
            loadUrl(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrl(url: String) {
        if (url.contains(".png",true)||url.contains(".jpg",true)
            ||url.contains(".jpeg",true)||url.contains(".jfif",true)||url.contains(".GIF",true)){
            binding.apply {
                isVisiableImage = true
                loadImage(imageView,url)
            }
        } else{
            binding.isVisiableImage = false
            binding.webView.apply {
                clearCache(true)
                clearHistory()
                webViewClient = AppWebViewClients()
                settings.javaScriptEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.builtInZoomControls = true
                settings.displayZoomControls = false
                fitsSystemWindows = true
                webViewClient = AppWebViewClients()
                binding.isVisiableProgress = true
                loadUrl("http://docs.google.com/gview?embedded=true&url=$url")
                binding.isVisiableProgress = true
            }
        }
    }

   inner class AppWebViewClients : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            // TODO Auto-generated method stub
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            // TODO Auto-generated method stub
            // function recall
            if (view.title.equals(""))
                view.reload()
            else
                binding.isVisiableProgress = false

            super.onPageFinished(view, url)
        }

       override fun onReceivedError(
           view: WebView?,
           request: WebResourceRequest?,
           error: WebResourceError?
       ) {
           binding.isVisiableProgress = false
           super.onReceivedError(view, request, error)
       }
    }





}