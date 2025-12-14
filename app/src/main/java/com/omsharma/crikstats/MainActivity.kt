package com.omsharma.crikstats

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.play.core.splitcompat.SplitCompat
import com.omsharma.crikstats.dynamicmodule.DynamicDeliveryCallback
import com.omsharma.crikstats.dynamicmodule.DynamicModuleDownloadUtil
import com.omsharma.crikstats.ui.components.DownloadModuleConfirmationDialog
import com.omsharma.crikstats.ui.components.DynamicModuleDownloadButton
import com.omsharma.crikstats.ui.theme.CrikStatsTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity(), DynamicDeliveryCallback {

    private val PLAYER_STATS_DYNAMIC_MODULE = "featureplayer"

    private lateinit var dynamicModuleDownloadUtil: DynamicModuleDownloadUtil

    private var logState = mutableStateOf("Ready to test.\n")
    private var dialogState = mutableStateOf(false)

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dynamicModuleDownloadUtil = DynamicModuleDownloadUtil(baseContext, this)

        setContent {
            CrikStatsTheme {
                ContentBody()
            }
        }
    }

    @Composable
    fun ContentBody() {
        // 1. The Download Button

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DynamicModuleDownloadButton {
                logState.value += "--> Button Clicked!\n"
                openPlayerStatsFeature()
            }
        }



        LazyColumn(modifier = Modifier.padding(top = 20.dp)) {
            item {
                Text(
                    text = logState.value,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }

        DownloadModuleConfirmationDialog(dialogState) {
            logState.value += "--> Dialog Confirmed. Starting Download...\n"
            downloadDynamicModule()
        }
    }

    private fun openPlayerStatsFeature() {
        val isDownloaded = dynamicModuleDownloadUtil.isModuleDownloaded(PLAYER_STATS_DYNAMIC_MODULE)

        if (isDownloaded) {
            logState.value += "Module found. Launching Activity...\n"
            startPlayerStatsActivity()
        } else {
            logState.value += "Module NOT found. Showing Confirmation Dialog.\n"
            dialogState.value = true
        }
    }

    private fun startPlayerStatsActivity() {
        try {
            val intent = Intent().setClassName(
                packageName,
                "com.omsharma.playerstats.PlayerStatsActivity"
            )
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("MainActivity", "Launch Failed", e)
            logState.value += "CRASH LAUNCHING ACTIVITY: ${e.message}\n"
        }
    }

    private fun downloadDynamicModule() {
        dynamicModuleDownloadUtil.downloadDynamicModule(PLAYER_STATS_DYNAMIC_MODULE)
    }

    override fun onDownloading() {
        val msg = "${getTimestamp()}: Status: DOWNLOADING..."
        logState.value += "$msg\n"
        Log.d("MainActivity", msg)
    }

    override fun onDownloadCompleted() {
        val msg = "${getTimestamp()}: Status: DOWNLOAD COMPLETE."
        logState.value += "$msg\n"
        Log.d("MainActivity", msg)
    }

    override fun onInstallSuccess() {
        val msg = "${getTimestamp()}: Status: INSTALL SUCCESS! Launching..."
        logState.value += "$msg\n"
        Log.d("MainActivity", msg)
        startPlayerStatsActivity()
    }

    override fun onFailed(errorMessage: String) {
        val msg = "${getTimestamp()}: FAILED: $errorMessage"
        logState.value += "$msg\n"
        Log.e("MainActivity", msg)
    }

    private fun getTimestamp(): String {
        val c = Calendar.getInstance()
        return "${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}:${c.get(Calendar.SECOND)}"
    }
}