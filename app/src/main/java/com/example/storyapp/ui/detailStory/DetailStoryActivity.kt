package com.example.storyapp.ui.detailStory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var story: ListStoryItem
    private lateinit var binding: ActivityDetailStoryBinding

    private val detailStoryViewModel: DetailStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        story = intent.getParcelableExtra(EXTRA_STORY)!!
        detailStoryViewModel.setDetailStory(story)
        displayResult()
    }

    private fun displayResult() {
        with(binding) {
            tvName.text = detailStoryViewModel.storyItem.name
            tvCreatedAt.text = detailStoryViewModel.storyItem.createdAt
            tvDescription.text = detailStoryViewModel.storyItem.description

            Glide.with(ivImage)
                .load(story.photoUrl) // URL Gambar
                .placeholder(R.drawable.ic_baseline_place_holder_24) // placeholder
                .error(R.drawable.ic_baseline_broken_image_24) // while error
                .fallback(R.drawable.ic_baseline_place_holder_24) // while null
                .circleCrop() // Mengubah image menjadi lingkaran
                .into(ivImage) // imageView mana yang akan diterapkan
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}