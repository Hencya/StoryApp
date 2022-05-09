package com.example.storyapp.ui.signUp

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class SignUpViewModel(val storyRepository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, pass: String) =
        storyRepository.register(name, email, pass)
}