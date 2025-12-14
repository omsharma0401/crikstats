package com.omsharma.playerstats.di

import com.omsharma.playerstats.data.remote.PlayerApiService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object PlayerInternalModule {

    @Provides
    fun providePlayerApiService(retrofit: Retrofit): PlayerApiService {
        return retrofit.create(PlayerApiService::class.java)
    }
}