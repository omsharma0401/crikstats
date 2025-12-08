package com.omsharma.playerstats.data.repo

import com.omsharma.crikstats.data.model.ApiResponse
import com.omsharma.crikstats.state.UiState
import com.omsharma.playerstats.data.model.PlayerData
import com.omsharma.playerstats.data.remote.PlayerApiService
import retrofit2.Retrofit
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class PlayerRepo @Inject constructor(
    private val retrofit: Retrofit
) {
    private val apiService: PlayerApiService by lazy {
        retrofit.create(PlayerApiService::class.java)
    }

    suspend fun getPlayerStats(): UiState<ApiResponse<List<PlayerData>>> {
        return try {
            val response = apiService.getPlayerStats()

            when {
                response.isSuccessful -> {
                    response.body()?.let { body ->
                        UiState.Success(body)
                    } ?: UiState.Failed("No Data Found")
                }
                response.code() in 500..599 -> {
                    UiState.Failed("Server error: ${response.code()}")
                }
                else -> {
                    UiState.Failed("API Error: ${response.code()} - ${response.message()}")
                }
            }
        } catch (e: UnknownHostException) {
            UiState.Failed("No Internet Connection")
        } catch (e: SocketTimeoutException) {
            UiState.Failed("Connection Timeout")
        } catch (e: IOException) {
            UiState.Failed("Network Error")
        } catch (e: Exception) {
            UiState.Failed("Unexpected error: ${e.message}")
        }
    }
}