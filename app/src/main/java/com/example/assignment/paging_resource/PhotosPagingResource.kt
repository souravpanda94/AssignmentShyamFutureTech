package com.example.assignment.paging_resource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.assignment.model.PhotosModelItem
import com.example.assignment.repository.PhotosRepository
import com.example.assignment.viewmodel.PhotosViewModel

class PhotosPagingSource(private val repository: PhotosRepository) : PagingSource<Int, PhotosModelItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotosModelItem> {
        try {
            var photosModelItem : List<PhotosModelItem> = ArrayList()
            val nextPage = params.key ?: 1
            val items = repository.getPhotos(nextPage, PhotosViewModel.PAGE_SIZE).collect{
                photosModelItem = it!!
            }

            return LoadResult.Page(
                data = photosModelItem,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (photosModelItem.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotosModelItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}