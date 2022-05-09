package com.example.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.repository.StoryRepository
import com.dicoding.storyapp.data.room.StoryDatabase
import com.example.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}