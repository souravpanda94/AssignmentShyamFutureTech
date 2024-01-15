package com.example.assignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.assignment.model.PhotosModelItem
import com.example.assignment.paging_resource.PhotosPagingSource
import com.example.assignment.repository.PhotosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(private val repository: PhotosRepository) : ViewModel() {

    fun getPhotosItems(): Flow<PagingData<PhotosModelItem>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PhotosPagingSource(repository) }
        ).flow.cachedIn(viewModelScope)
    }
    companion object {
        const val PAGE_SIZE = 20
    }
}