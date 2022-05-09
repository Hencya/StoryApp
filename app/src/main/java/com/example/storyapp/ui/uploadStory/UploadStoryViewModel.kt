package com.example.storyapp.ui.uploadStory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun uploadStory(
        token: String,
        description: RequestBody,
        imageMultipart: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = storyRepository.uploadStory(token, description, imageMultipart, lat, lon)
}