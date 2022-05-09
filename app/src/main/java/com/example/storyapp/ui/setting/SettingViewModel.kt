package com.example.storyapp.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: LoginPreference) : ViewModel() {

    fun getUser(): Flow<UserModel> {
        return pref.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}