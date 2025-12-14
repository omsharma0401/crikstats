package com.omsharma.crikstats.dynamicmodule

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallException
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.google.android.play.core.splitinstall.testing.FakeSplitInstallManagerFactory
import com.omsharma.crikstats.BuildConfig

class DynamicModuleDownloadUtil(
    context: Context,
    private val callback: DynamicDeliveryCallback,
    private val isLocalTesting: Boolean = BuildConfig.DEBUG
) {

    private var splitInstallManager: SplitInstallManager
    private var mySessionId = 0
    private var installListener: SplitInstallStateUpdatedListener? = null

    init {
        try {
            if (isLocalTesting) {
                splitInstallManager = FakeSplitInstallManagerFactory.create(context)
            } else {
                splitInstallManager = SplitInstallManagerFactory.create(context)
            }
        } catch (e: Exception) {
            splitInstallManager = SplitInstallManagerFactory.create(context)
        }
    }

    fun isModuleDownloaded(moduleName: String): Boolean {
        return splitInstallManager.installedModules.contains(moduleName)
    }

    fun downloadDynamicModule(moduleName: String) {
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        installListener = SplitInstallStateUpdatedListener { state -> handleInstallStates(state) }
        installListener?.let { splitInstallManager.registerListener(it) }

        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                mySessionId = sessionId
            }
            .addOnFailureListener { e ->
                unregisterListener()
                if (e is SplitInstallException) {
                    handleInstallFailure(e.errorCode)
                } else {
                    callback.onFailed("Exception: ${e.message}")
                }
            }
    }

    private fun unregisterListener() {
        installListener?.let {
            splitInstallManager.unregisterListener(it)
            installListener = null
        }
    }

    private fun handleInstallFailure(errorCode: Int) {
        val message = when (errorCode) {
            SplitInstallErrorCode.NETWORK_ERROR -> "Network Error"
            SplitInstallErrorCode.MODULE_UNAVAILABLE -> "Module Unavailable"
            SplitInstallErrorCode.INSUFFICIENT_STORAGE -> "Insufficient Storage"
            SplitInstallErrorCode.ACCESS_DENIED -> "Access Denied"
            else -> "Error Code: $errorCode"
        }
        callback.onFailed(message)
    }

    private fun handleInstallStates(state: SplitInstallSessionState) {
        if (state.sessionId() == mySessionId) {
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> callback.onDownloading()
                SplitInstallSessionStatus.DOWNLOADED -> callback.onDownloadCompleted()
                SplitInstallSessionStatus.INSTALLED -> {
                    unregisterListener()
                    callback.onInstallSuccess()
                }

                SplitInstallSessionStatus.FAILED -> {
                    unregisterListener()
                    callback.onFailed("Installation Failed Code: ${state.errorCode()}")
                }

                else -> {}
            }
        }
    }
}