package com.omsharma.crikstats

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omsharma.crikstats.dynamicmodule.DynamicDeliveryCallback
import com.omsharma.crikstats.dynamicmodule.DynamicModuleDownloadUtil
import com.omsharma.crikstats.ui.components.DownloadModuleConfirmationDialog
import com.omsharma.crikstats.ui.components.DynamicModuleDownloadButton
import com.omsharma.crikstats.ui.theme.CrikStatsTheme
import com.omsharma.crikstats.util.Constants.BASE_MODULE
import com.omsharma.crikstats.util.Constants.FEATURE_MODULE
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity(), DynamicDeliveryCallback {

    private val PLAYER_STATS_DYNAMIC_MODULE = "player_stats_module"
    private lateinit var dynamicModuleDownloadUtil: DynamicModuleDownloadUtil
    private var logState = mutableStateOf("Activity Log:\n")

    private var dialogState = mutableStateOf(false)


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
        DynamicModuleDownloadButton {
            openPlayerStatsFeature()
        }

        DownloadModuleConfirmationDialog(dialogState, ::downloadDynamicModule)
    }

    private fun openPlayerStatsFeature() {
        if (dynamicModuleDownloadUtil.isModuleDownloaded(PLAYER_STATS_DYNAMIC_MODULE)) {
            logState.value += "${getCurrentTimestamp()}: Module is already downloaded.\n"
            startPlayerStatsActivity()
        } else {
            dialogState.value = true
        }
    }

    private fun startPlayerStatsActivity() {
        val intent = Intent()
        intent.setClassName(
            BASE_MODULE,
            FEATURE_MODULE
        )
        startActivity(intent)
    }

    private fun downloadDynamicModule() {
        logState.value += "${getCurrentTimestamp()}: Call for download.\n"
        dynamicModuleDownloadUtil.downloadDynamicModule(PLAYER_STATS_DYNAMIC_MODULE)
    }

    override fun onDownloading() {
        logState.value += "${getCurrentTimestamp()}: Downloading...\n"
    }

    override fun onDownloadCompleted() {
        logState.value += "${getCurrentTimestamp()}: Module download completed.\n"
    }

    override fun onInstallSuccess() {
        logState.value += "${getCurrentTimestamp()}: Module install Success!\n"
        startPlayerStatsActivity()
    }

    override fun onFailed(errorMessage: String) {
        logState.value += "${getCurrentTimestamp()}: $errorMessage\n"
    }

    private fun getCurrentTimestamp(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.HOUR).toString().padStart(2, '0')}:" +
                "${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}:" +
                calendar.get(Calendar.SECOND).toString().padStart(2, '0')
    }
}