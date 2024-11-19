package test.dapuk.dicodingstory.data.repository

import android.content.SharedPreferences
import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody
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