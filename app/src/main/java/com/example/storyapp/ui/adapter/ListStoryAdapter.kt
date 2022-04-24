package com.example.storyapp.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ItemRowStoriesBinding
import com.example.storyapp.ui.detailStory.DetailStoryActivity
import com.example.storyapp.utils.StoryDiffCallback
import com.example.storyapp.utils.formatDate
import java.util.*

class ListStoryAdapter : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    private val listStory = java.util.ArrayList<ListStoryItem>()

    fun setListStory(itemStory: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(this.listStory, itemStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listStory.clear()
        this.listStory.addAll(itemStory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    inner class ViewHolder(private var binding: ItemRowStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            Log.d("Masuk Sini View holder", story.toString())
            Glide.with(binding.root.context)
                .load(story.photoUrl) // URL Gambar
                .placeholder(R.drawable.ic_baseline_place_holder_24) // placeholder
                .error(R.drawable.ic_baseline_broken_image_24) // while error
                .fallback(R.drawable.ic_baseline_place_holder_24) // while null
                .into(binding.imgItemImage) // imageView mana yang akan diterapkan
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description
            binding.tvItemCreatedAt.text = binding.root.resources.getString(
                R.string.created_at,
                formatDate(story.createdAt, TimeZone.getDefault().id)
            )
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = listStory.size
}
