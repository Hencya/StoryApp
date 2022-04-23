package com.example.storyapp.ui.detailStory

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.remote.response.ListStoryItem

class DetailStoryViewModel : ViewModel() {
    lateinit var storyItem: ListStoryItem

    fun setDetailStory(story: ListStoryItem): ListStoryItem {
        storyItem = story
        return storyItem
    }
}