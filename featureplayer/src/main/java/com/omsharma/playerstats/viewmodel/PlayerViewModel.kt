package com.omsharma.playerstats.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omsharma.crikstats.state.UiState
import com.omsharma.playerstats.data.model.PlayerData
import com.omsharma.playerstats.data.repo.PlayerRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val playerRepo: PlayerRepo
) : ViewModel() {

    private val _playerListState = MutableStateFlow<UiState<List<PlayerData>>>(UiState.Idle)
    val playerListState: StateFlow<UiState<List<PlayerData>>> = _playerListState.asStateFlow()

    init {
        getAllPlayers()
    }

    fun getAllPlayers() {
        viewModelScope.launch {
            _playerListState.value = UiState.Loading

            when (val result = playerRepo.getPlayerStats()) {
                is UiState.Success -> {
                    _playerListState.value = UiState.Success(result.data.body)
                }
                is UiState.Failed -> {
                    _playerListState.value = UiState.Failed(result.message)
                }
                else -> {}
            }
        }
    }
}