package com.omsharma.crikstats

import android.app.Application
import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CrikStatsApp : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // 👇 THIS IS THE MAGIC LINE. Without it, the app can't see the new module!
        SplitCompat.install(this)
    }
}