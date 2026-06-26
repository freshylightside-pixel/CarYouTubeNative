package com.just.youtube

import android.content.Context
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.view.Surface
import android.webkit.WebView
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.Screen
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.SurfaceCallback

class MyCarService : CarAppService() {
    override fun onCreateSession(): Session = YouTubeSession()
}

class YouTubeSession : Session(), SurfaceCallback {
    private var virtualDisplay: VirtualDisplay? = null
    private var webView: WebView? = null

    override fun onCreateScreen(intent: android.content.Intent): Screen {
        return object : Screen(carContext) {
            override fun onGetTemplate(): Template {
                carContext.getCarService(androidx.car.app.navigation.NavigationManager::class.java)
                    .setSurfaceCallback(this@YouTubeSession)
                return NavigationTemplate.Builder().build()
            }
        }
    }

    override fun onSurfaceAvailable(surface: Surface) {
        val dm = carContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        webView = WebView(carContext).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.userAgentString = "Mozilla/5.0 (Linux; Android 15) Chrome/126.0.0.0 Mobile Safari/537.36"
            loadUrl("https://m.youtube.com")
        }
        virtualDisplay = dm.createVirtualDisplay(
            "CarYouTubeDisplay", 800, 480, 160, surface,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
        )
    }

    override fun onSurfaceDestroyed(surface: Surface) {
        virtualDisplay?.release()
        virtualDisplay = null
        webView = null
    }
}
