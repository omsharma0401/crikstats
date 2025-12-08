package com.omsharma.playerstats.data.remote

import com.omsharma.crikstats.data.model.ApiResponse
import com.omsharma.playerstats.data.model.PlayerData
import com.omsharma.playerstats.data.util.Constants
import retrofit2.Response
import retrofit2.http.GET

interface PlayerApiService {
    @GET(Constants.PLAYER_STATS_ENDPOINT)
    suspend fun getPlayerStats(): Response<ApiResponse<List<PlayerData>>>
}