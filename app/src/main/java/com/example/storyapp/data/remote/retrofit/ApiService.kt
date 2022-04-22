package com.example.storyapp.data.remote.retrofit

import com.example.storyapp.data.remote.request.LoginRequest
import com.example.storyapp.data.remote.request.RegisterRequest
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") des: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<StoriesResponse>
}