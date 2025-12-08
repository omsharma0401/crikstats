package com.omsharma.playerstats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.omsharma.crikstats.di.PlayerModuleDependencies
import com.omsharma.playerstats.di.DaggerPlayerComponent
import com.omsharma.playerstats.ui.screens.PlayerStatsScreen
import com.omsharma.playerstats.viewmodel.PlayerViewModel
import com.omsharma.playerstats.viewmodel.PlayerViewModelFactory
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class PlayerStatsActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: PlayerViewModelFactory

    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerPlayerComponent.builder()
            .context(this)
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    applicationContext,
                    PlayerModuleDependencies::class.java
                )
            )
            .build()
            .inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]

        setContent {
            PlayerStatsScreen(
                viewModel = viewModel,
                onBack = { finish() }
            )
        }
    }
}