package com.example.storyapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.storyapp.data.remote.response.ListStoryItem

class StoryDiffCallback(
    private val mOldStoriesList: List<ListStoryItem>,
    private val mNewStoriesList: List<ListStoryItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = mOldStoriesList.size

    override fun getNewListSize() = mNewStoriesList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        mOldStoriesList[oldItemPosition].id == mNewStoriesList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldStory = mOldStoriesList[oldItemPosition]
        val newStory = mNewStoriesList[newItemPosition]
        return oldStory.id == newStory.id
    }
}