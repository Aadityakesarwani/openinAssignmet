package com.innovativetools.assignment.data.network.api

import com.innovativetools.assignment.data.model.ApiResponse
import com.innovativetools.assignment.data.model.RequestBody
import retrofit2.http.*

interface ApiServiceInterface {
    @GET(ApiEndpoints.DASHBOARD)
    suspend fun getDashboardData(): ApiResponse
    @POST
    suspend fun postData(@Url url: String, @Body requestBody: RequestBody): ApiResponse
}