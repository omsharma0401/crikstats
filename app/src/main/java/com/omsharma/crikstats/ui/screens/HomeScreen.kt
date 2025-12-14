package com.omsharma.crikstats.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omsharma.crikstats.ui.components.CustomButton
import com.omsharma.crikstats.ui.theme.metropolisFamily
import com.omsharma.crikstats.util.Modules
import com.omsharma.crikstats.util.Routes
import com.omsharma.crikstats.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val isDownloading = viewModel.isDownloading.value
    val errorMessage = viewModel.errorMessage.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CrikStats Dashboard", fontFamily = metropolisFamily) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to CrikStats",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontFamily = metropolisFamily
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isDownloading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Downloading Module...", fontFamily = metropolisFamily)
            } else {
                CustomButton(
                    text = "Open Player Stats",
                    onClick = {
                        viewModel.onOpenPlayerStatsClick(
                            moduleName = Modules.PLAYER_STATS,
                            activityName = Routes.PLAYER_ACTIVITY
                        )
                    }
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = metropolisFamily
                )
            }
        }
    }
}