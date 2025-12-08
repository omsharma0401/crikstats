package com.omsharma.crikstats.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.omsharma.crikstats.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(context)
    private val MODULE_NAME = "featureplayer"

    private val _downloadState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val downloadState: StateFlow<UiState<Boolean>> = _downloadState

    fun downloadFeature() {
        if (splitInstallManager.installedModules.contains(MODULE_NAME)) {
            _downloadState.value = UiState.Success(true)
            return
        }

        _downloadState.value = UiState.Loading

        val request = SplitInstallRequest.newBuilder()
            .addModule(MODULE_NAME)
            .build()

        splitInstallManager.startInstall(request)
            .addOnSuccessListener {
                _downloadState.value = UiState.Success(true)
            }
            .addOnFailureListener {
                _downloadState.value = UiState.Failed(it.message ?: "Download failed")
            }
    }

    fun resetState() {
        _downloadState.value = UiState.Idle
    }
}