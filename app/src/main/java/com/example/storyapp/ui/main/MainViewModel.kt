package com.example.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoriesResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: LoginPreference) : ViewModel() {

    private val _itemStory = MutableLiveData<List<ListStoryItem>>()
    val itemStory: LiveData<List<ListStoryItem>> = _itemStory

    private val _isHaveData = MutableLiveData<Boolean>()
    val isHaveData: LiveData<Boolean> = _isHaveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun showListStory(token: String) {
        _isHaveData.value = true
        _isLoading.value = true
        val client = ApiConfig
            .getApiService()
            .getStories("Bearer $token")

        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _itemStory.value = response.body()?.listStory
                            _isHaveData.value =
                                responseBody.message == "Stories fetched successfully"
                        }
                    }
                } else {
                    Log.e(TAG, "Error Message: ${response.message()}")
                    _snackBarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Error Message: ${t.message}")
                _snackBarText.value = Event(t.message.toString())
            }
        })
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}