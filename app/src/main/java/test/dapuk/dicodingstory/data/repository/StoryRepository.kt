package test.dapuk.dicodingstory.data.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import test.dapuk.dicodingstory.data.paging.storyPagingSource
import test.dapuk.dicodingstory.data.response.ListStoryItem
import test.dapuk.dicodingstory.data.response.RegisterResponse
import test.dapuk.dicodingstory.data.response.StoryDetailResponse
import test.dapuk.dicodingstory.data.response.StoryResponse
import test.dapuk.dicodingstory.data.retrofit.ApiService

class StoryRepository(
    private val apiServide: ApiService,
    private val sharedPref: SharedPreferences
) {

    suspend fun getStories(): StoryResponse? {
        val token = sharedPref.getString("token", null)
        if (token != null) {
            return apiServide.getStories("Bearer $token")
        } else {
            Log.e("StoryRepo", "tokennya null cok")
            return null
        }
    }

  fun getStoriesPaging() : LiveData<PagingData<ListStoryItem>>{

        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                storyPagingSource(apiServide,sharedPref)
            }
        ).liveData
    }

    suspend fun getStoriesLocation(): StoryResponse? {
        val token = sharedPref.getString("token", null)
        if (token != null) {
            return apiServide.getStoriesLocation("Bearer $token")
        } else {
            Log.e("StoryRepo", "tokennya null cok")
            return null
        }
    }

    suspend fun getStoriesDetail(id: String): StoryDetailResponse? {
        val token = sharedPref.getString("token", null)
        if (token != null) {
            return apiServide.getStoriesDetail("Bearer $token", id)
        } else {
            Log.e("StoryRepo", "tokennya null cok")
            return null
        }
    }

    suspend fun addStory(file: MultipartBody.Part, description: RequestBody): RegisterResponse? {
        val token = sharedPref.getString("token", null)
        if (token != null) {
            return apiServide.addStory("Bearer $token", file, description)
        } else {
            Log.e("StoryRepo", "tokennya null cok")
            return null
        }
    }
}