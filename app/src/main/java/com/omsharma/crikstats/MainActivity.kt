package com.omsharma.crikstats

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.play.core.splitcompat.SplitCompat
import com.omsharma.crikstats.ui.screens.HomeScreen
import com.omsharma.crikstats.ui.theme.CrikStatsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CrikStatsTheme {
                HomeScreen()
            }
        }
    }
}