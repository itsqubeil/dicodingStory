package test.dapuk.dicodingstory.data.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import test.dapuk.dicodingstory.data.response.LoginResponse
import test.dapuk.dicodingstory.data.response.RegisterResponse
import test.dapuk.dicodingstory.data.response.StoryDetailResponse
import test.dapuk.dicodingstory.data.response.StoryResponse

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse


    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") authToken: String
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): RegisterResponse

    @GET("stories/{id}")
    suspend fun getStoriesDetail(
        @Header("Authorization") authToken: String,
        @Path("id") id: String
    ): StoryDetailResponse

}