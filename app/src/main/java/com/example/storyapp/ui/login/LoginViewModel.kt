package com.example.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, pass: String) =
        storyRepository.login(email, pass)
}