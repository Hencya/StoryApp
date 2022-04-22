package com.example.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResult,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Boolean
)

data class LoginResult(

    @field:SerializedName("token")
    val token: String,


    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,
)