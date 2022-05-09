package com.example.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.repository.StoryRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getPagingStories(token).cachedIn(viewModelScope).asLiveData()
    }
}