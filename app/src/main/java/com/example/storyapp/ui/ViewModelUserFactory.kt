package com.example.storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.ui.main.MainPrefViewModel
import com.example.storyapp.ui.setting.SettingViewModel

class ViewModelUserFactory(private val pref: LoginPreference) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainPrefViewModel::class.java) -> {
                MainPrefViewModel(pref) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}