package com.example.storyapp.ui.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

class ListStoryAdapter(

) :
    RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    private val listStories = ArrayList<ListStoryItem>()

    fun setListStory(itemStory: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(this.listStories, itemStory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.listStories.clear()
        this.listStories.addAll(itemStory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRowStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(listStories[position])
    }

    class ViewHolder(private var binding: ItemRowStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(story: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(story.photoUrl) // URL Gambar
                .placeholder(R.drawable.ic_baseline_place_holder_24) // placeholder
                .error(R.drawable.ic_baseline_broken_image_24) // while error
                .fallback(R.drawable.ic_baseline_place_holder_24) // while null
                .circleCrop() // Mengubah image menjadi lingkaran
                .into(binding.imgItemImage) // imageView mana yang akan diterapkan
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description
            binding.tvItemCreatedAt.text = story.createdAt
            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = listStories.size
}