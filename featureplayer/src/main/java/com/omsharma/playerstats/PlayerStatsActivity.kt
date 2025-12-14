package com.omsharma.playerstats

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.play.core.splitcompat.SplitCompat
import com.omsharma.crikstats.di.PlayerModuleDependencies
import com.omsharma.playerstats.di.DaggerPlayerComponent
import com.omsharma.playerstats.ui.screens.PlayerStatsScreen
import com.omsharma.playerstats.ui.theme.CrikStatsTheme
import com.omsharma.playerstats.viewmodel.PlayerViewModel
import com.omsharma.playerstats.viewmodel.PlayerViewModelFactory
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class PlayerStatsActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: PlayerViewModelFactory

    private val viewModel: PlayerViewModel by viewModels { viewModelFactory }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)

        setContent {
            CrikStatsTheme {
                PlayerStatsScreen(
                    viewModel = viewModel,
                    onBack = { finish() }
                )
            }
        }
    }

    private fun injectDependencies() {
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
    }
}