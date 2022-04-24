package com.example.storyapp.ui.detailStory

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.formatDate
import java.util.*

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
            tvCreatedAt.text =
                getString(
                    R.string.created_at, formatDate(
                        detailStoryViewModel.storyItem.createdAt,
                        TimeZone.getDefault().id
                    )
                )
            tvDescription.text = detailStoryViewModel.storyItem.description

            Glide.with(ivImage)
                .load(story.photoUrl) // URL Gambar
                .placeholder(R.drawable.ic_baseline_place_holder_24) // placeholder
                .error(R.drawable.ic_baseline_broken_image_24) // while error
                .fallback(R.drawable.ic_baseline_place_holder_24) // while null
                .into(ivImage) // imageView mana yang akan diterapkan
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}