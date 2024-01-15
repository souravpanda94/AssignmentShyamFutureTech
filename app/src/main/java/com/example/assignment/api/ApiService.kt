package com.example.assignment.api

import com.example.assignment.model.PhotosModelItem
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v2/list")
    suspend fun getItems(@Query("page") page: Int, @Query("limit") limit: Int): Response<List<PhotosModelItem>>
}