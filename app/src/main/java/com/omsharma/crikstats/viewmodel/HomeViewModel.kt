package com.omsharma.crikstats.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.omsharma.crikstats.dynamicmodule.DynamicDeliveryCallback
import com.omsharma.crikstats.dynamicmodule.DynamicModuleDownloadUtil
import com.omsharma.crikstats.navigation.DynamicFeatureNavigator

class HomeViewModel(application: Application) : AndroidViewModel(application), DynamicDeliveryCallback {

    private val navigator = DynamicFeatureNavigator(application)
    private val downloadUtil = DynamicModuleDownloadUtil(application, this)

    private val _isDownloading = mutableStateOf(false)
    val isDownloading: State<Boolean> = _isDownloading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private var pendingActivityName = ""

    fun onOpenPlayerStatsClick(moduleName: String, activityName: String) {
        if (downloadUtil.isModuleDownloaded(moduleName)) {
            navigator.launchActivity(activityName)
        } else {
            pendingActivityName = activityName
            _errorMessage.value = null
            downloadUtil.downloadDynamicModule(moduleName)
        }
    }

    override fun onDownloading() {
        _isDownloading.value = true
    }

    override fun onDownloadCompleted() {
        _isDownloading.value = true
    }

    override fun onInstallSuccess() {
        _isDownloading.value = false
        if (pendingActivityName.isNotEmpty()) {
            navigator.launchActivity(pendingActivityName)
        }
    }

    override fun onFailed(error: String) {
        _isDownloading.value = false
        _errorMessage.value = error
    }
}