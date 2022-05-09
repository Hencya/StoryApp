package com.example.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(token: String) = storyRepository.getStoryMap(token)
}