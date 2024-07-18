package com.innovativetools.assignment.data.repository

import com.innovativetools.assignment.data.model.ApiResponse
import com.innovativetools.assignment.data.model.RequestBody
import com.innovativetools.assignment.data.network.api.ApiServiceInterface

class DashboardRepository(private val apiService: ApiServiceInterface) {

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