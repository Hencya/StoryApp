package com.example.storyapp.ui.main

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import kotlinx.coroutines.flow.Flow

class MainPrefViewModel(private val pref: LoginPreference) : ViewModel() {
    fun getUser(): Flow<UserModel> {
        return pref.getUser()
    }
}