package com.omsharma.playerstats.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omsharma.crikstats.state.UiState
import com.omsharma.crikstats.ui.theme.metropolisFamily
import com.omsharma.playerstats.ui.components.ErrorContent
import com.omsharma.playerstats.ui.components.LoadingContent
import com.omsharma.playerstats.ui.components.PlayerList
import com.omsharma.playerstats.ui.components.TopBar
import com.omsharma.playerstats.viewmodel.PlayerViewModel

@Composable
fun PlayerStatsScreen(
    viewModel: PlayerViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.playerListState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                title = "Team India Squad",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is UiState.Loading -> LoadingContent()
                is UiState.Success -> PlayerList(players = currentState.data)
                is UiState.Failed -> ErrorContent(
                    message = currentState.message,
                    onRetry = { viewModel.getAllPlayers() }
                )

                else -> Text(
                    text = "Initializing...",
                    fontFamily = metropolisFamily
                )
            }
        }
    }
}