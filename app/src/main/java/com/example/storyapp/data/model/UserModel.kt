package com.example.storyapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val userId: String,
    val name: String,
    val email: String,
    val password: String,
    val token: String,
    val isLoggedIn: Boolean
) : Parcelable