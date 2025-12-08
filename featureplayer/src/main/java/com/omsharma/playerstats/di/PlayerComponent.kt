package com.omsharma.playerstats.di

import android.content.Context
import com.omsharma.crikstats.di.PlayerModuleDependencies
import com.omsharma.playerstats.PlayerStatsActivity
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [PlayerModuleDependencies::class])
interface PlayerComponent {
    fun inject(activity: PlayerStatsActivity)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(dependencies: PlayerModuleDependencies): Builder
        fun build(): PlayerComponent
    }
}