package com.omsharma.playerstats

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
// 👇 CRITICAL IMPORT
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

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(this)
    }

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

        val viewModel = ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]

        setContent {
            CrikStatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerStatsScreen(
                        viewModel = viewModel,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}