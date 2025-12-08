package com.omsharma.crikstats.data.util

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class DynamicModuleHelper(context: Context) {
    private val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(context)
    val moduleName = "featureplayer"

    private var listener: SplitInstallStateUpdatedListener? = null

    fun isModuleInstalled(): Boolean {
        return splitInstallManager.installedModules.contains(moduleName)
    }

    fun downloadModule(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
        onProgress: ((Int) -> Unit)? = null
    ) {
        if (isModuleInstalled()) {
            onSuccess()
            return
        }

        // Create and register listener for installation updates
        listener = SplitInstallStateUpdatedListener { state ->
            when (state.status()) {
                SplitInstallSessionStatus.INSTALLED -> {
                    onSuccess()
                    listener?.let { splitInstallManager.unregisterListener(it) }
                }
                SplitInstallSessionStatus.DOWNLOADING -> {
                    if (state.totalBytesToDownload() > 0) {
                        val progress = (state.bytesDownloaded() * 100 / state.totalBytesToDownload()).toInt()
                        onProgress?.invoke(progress)
                    }
                }
                SplitInstallSessionStatus.FAILED -> {
                    onFailure(Exception("Installation failed with error code: ${state.errorCode()}"))
                    listener?.let { splitInstallManager.unregisterListener(it) }
                }
                SplitInstallSessionStatus.CANCELED -> {
                    onFailure(Exception("Installation canceled"))
                    listener?.let { splitInstallManager.unregisterListener(it) }
                }
            }
        }

        listener?.let { splitInstallManager.registerListener(it) }

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnFailureListener { exception ->
                onFailure(exception)
                listener?.let { splitInstallManager.unregisterListener(it) }
            }
    }

    fun cleanup() {
        listener?.let { splitInstallManager.unregisterListener(it) }
    }
}