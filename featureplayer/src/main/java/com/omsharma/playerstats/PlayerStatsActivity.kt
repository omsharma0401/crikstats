package com.omsharma.playerstats

//class PlayerStatsActivity : ComponentActivity() {
//
//    @Inject
//    lateinit var viewModelFactory: PlayerViewModelFactory
//
//    private lateinit var viewModel: PlayerViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        DaggerPlayerComponent.builder()
//            .context(this)
//            .appDependencies(
//                EntryPointAccessors.fromApplication(
//                    applicationContext,
//                    PlayerModuleDependencies::class.java
//                )
//            )
//            .build()
//            .inject(this)
//
//        viewModel = ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]
//
//        setContent {
//            PlayerStatsScreen(
//                viewModel = viewModel,
//                onBack = { finish() }
//            )
//        }
//    }
//}

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
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

        val viewModel =
            ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]

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