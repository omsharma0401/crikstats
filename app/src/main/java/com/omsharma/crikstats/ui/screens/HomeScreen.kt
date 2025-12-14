package com.omsharma.crikstats.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.omsharma.crikstats.state.UiState
import com.omsharma.crikstats.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.downloadState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("crikstats://player"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CrikStats Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Downloading Module...")
            }
            is UiState.Failed -> {
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.downloadFeature() }) { Text("Retry") }
            }
            else -> {
                Button(onClick = { viewModel.downloadFeature() }) {
                    Text("Download Player Module")
                }
            }
        }
    }
}