package com.omsharma.crikstats.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerModuleDependencies {
    fun provideRetrofit(): Retrofit
}