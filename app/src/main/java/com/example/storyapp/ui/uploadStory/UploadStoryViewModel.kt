package com.example.storyapp.ui.uploadStory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.remote.response.UploadResponse
import com.example.storyapp.data.remote.retrofit.ApiConfig
import com.example.storyapp.utils.ApiCallbackString
import okhttp3.MultipartBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadStoryViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun uploadImage(
        user: UserModel,
        description: String,
        imageMultipart: MultipartBody.Part,
        callback: ApiCallbackString
    ) {
        _isLoading.value = true
        val service = ApiConfig().getApiService()
            .uploadStory("Bearer ${user.token}", description, imageMultipart)
        service.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        callback.onResponse(response.body() != null, SUCCESS)
                    }
                } else {
                    Log.e(TAG, "Error Message: ${response.message()}")

                    val jsonObject =
                        JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject

                    val message = jsonObject.getString("message")
                    callback.onResponse(false, message)
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Error Message: ${t.message}")
                callback.onResponse(false, t.message.toString())
            }
        })

    }

    companion object {
        private const val TAG = "UploadStoryViewModel"
        private const val SUCCESS = "success"
    }
}