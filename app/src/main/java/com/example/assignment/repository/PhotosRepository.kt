package com.example.assignment.repository

import com.example.assignment.api.ApiService
import com.example.assignment.model.PhotosModelItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PhotosRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPhotos(page: Int, limit: Int): Flow<List<PhotosModelItem>?> = flow {
        val response = apiService.getItems(page, limit).body()
        emit(response)
    }.flowOn(Dispatchers.IO)
}