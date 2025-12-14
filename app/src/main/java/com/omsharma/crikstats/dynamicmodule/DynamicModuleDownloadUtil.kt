package com.omsharma.crikstats.dynamicmodule

import android.content.Context
import android.util.Log
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

const val TAG = "DynamicModuleUtil"

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
                Log.d(TAG, "INIT: Using FakeSplitInstallManager (Local Testing)")
            } else {
                splitInstallManager = SplitInstallManagerFactory.create(context)
                Log.d(TAG, "INIT: Using Real SplitInstallManager (Release)")
            }
        } catch (e: Exception) {
            Log.w(
                TAG,
                "INIT WARNING: Failed to load FakeManager. Falling back to Real Manager. (Did you use 'Run' instead of bundletool?)"
            )
            splitInstallManager = SplitInstallManagerFactory.create(context)
        }
    }

    fun isModuleDownloaded(moduleName: String): Boolean {
        val installed = splitInstallManager.installedModules.contains(moduleName)
        Log.d(TAG, "CHECK: Is module '$moduleName' installed? -> $installed")
        return installed
    }

    fun downloadDynamicModule(moduleName: String) {
        Log.d(TAG, "DOWNLOAD: Starting request for '$moduleName'...")

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        installListener = SplitInstallStateUpdatedListener { state -> handleInstallStates(state) }
        installListener?.let { splitInstallManager.registerListener(it) }

        splitInstallManager.startInstall(request)
            .addOnSuccessListener { sessionId ->
                mySessionId = sessionId
                Log.d(TAG, "DOWNLOAD: Request Accepted. Session ID: $sessionId")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "DOWNLOAD: Request Failed", e)
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
            SplitInstallErrorCode.MODULE_UNAVAILABLE -> "Module Unavailable (Check Gradle Name!)"
            SplitInstallErrorCode.INSUFFICIENT_STORAGE -> "Insufficient Storage"
            SplitInstallErrorCode.ACCESS_DENIED -> "Access Denied (Permissions?)"
            else -> "Error Code: $errorCode"
        }
        Log.e(TAG, "DOWNLOAD FAILURE: $message")
        callback.onFailed(message)
    }

    private fun handleInstallStates(state: SplitInstallSessionState) {
        if (state.sessionId() == mySessionId) {
            Log.d(TAG, "STATE UPDATE: Status = ${state.status()}")

            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> callback.onDownloading()
                SplitInstallSessionStatus.DOWNLOADED -> callback.onDownloadCompleted()
                SplitInstallSessionStatus.INSTALLED -> {
                    Log.d(TAG, "STATE: Installed confirmed.")
                    unregisterListener()
                    callback.onInstallSuccess()
                }

                SplitInstallSessionStatus.FAILED -> {
                    Log.e(TAG, "STATE: Failed (Error code: ${state.errorCode()})")
                    unregisterListener()
                    callback.onFailed("Installation Failed Code: ${state.errorCode()}")
                }

                else -> {}
            }
        }
    }
}