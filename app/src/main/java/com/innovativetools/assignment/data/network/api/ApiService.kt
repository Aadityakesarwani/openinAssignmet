package com.innovativetools.assignment.data.network.api


import com.innovativetools.assignment.data.model.ApiResponse
import com.innovativetools.assignment.data.model.RequestBody
import com.innovativetools.assignment.data.network.auth.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object ApiService {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiEndpoints.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor())
            .build()
    }

    val apiService: ApiServiceInterface by lazy {
        retrofit.create(ApiServiceInterface::class.java)
    }


    suspend fun getDashboardData(): ApiResponse? {
        return try {
            apiService.getDashboardData()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun postData(apiUrl: String, requestBody: RequestBody): ApiResponse? {
        return try {
            apiService.postData(apiUrl, requestBody)
        } catch (e: Exception) {
            null
        }
    }
}
