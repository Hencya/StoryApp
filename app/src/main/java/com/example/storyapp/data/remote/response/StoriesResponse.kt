package com.example.storyapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StoriesResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: Boolean
)

@Parcelize
data class ListStoryItem(

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("id")
    val id: String,
) : Parcelable