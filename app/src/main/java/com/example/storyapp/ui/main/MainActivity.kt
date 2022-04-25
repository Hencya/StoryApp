package com.example.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.adapter.ListStoryAdapter
import com.example.storyapp.ui.setting.SettingActivity
import com.example.storyapp.ui.uploadStory.UploadStoryActivity
import com.example.storyapp.ui.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("login_pref")


class MainActivity : AppCompatActivity() {
    private lateinit var user: UserModel
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: ListStoryAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = getString(R.string.main_title)

        adapter = ListStoryAdapter()
        setupViewModel()

        setupAction()
        setupRecycleView()
        showSnackBar()
        showLoading()
    }


    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginPreference.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) {
            user = UserModel(
                it.userId,
                it.name,
                it.email,
                it.password,
                it.token,
                true
            )
            if (!user.isLoggedIn) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            binding?.tvName?.text = getString(R.string.hallo_user, user.name)

            mainViewModel.showListStory(user.token)
            mainViewModel.itemStory.observe(this) {
                adapter.setListStory(it)
            }
        }
    }


    private fun showSnackBar() {
        mainViewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(
                    findViewById(R.id.rv_stories),
                    snackBarText,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun setupAction() {
        binding?.fabAdd?.setOnClickListener { view ->
            if (view.id == R.id.fab_add) {
                mainViewModel.getUser().observe(this) {
                    user = UserModel(
                        it.userId,
                        it.name,
                        it.email,
                        it.password,
                        it.token,
                        true
                    )
                    val intent = Intent(this@MainActivity, UploadStoryActivity::class.java)
                    intent.putExtra(UploadStoryActivity.DATA_USER, user)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        binding?.rvStories?.layoutManager = layoutManager
        binding?.rvStories?.setHasFixedSize(true)
        binding?.rvStories?.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
                val settingActivityIntent = Intent(this, SettingActivity::class.java)
                startActivity(settingActivityIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading() {
        mainViewModel.isLoading.observe(this) {
            binding?.apply {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    rvStories.visibility = View.INVISIBLE
                } else {
                    progressBar.visibility = View.GONE
                    rvStories.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getUser().observe(this) {
            user = UserModel(
                it.userId,
                it.name,
                it.email,
                it.password,
                it.token,
                true
            )

            mainViewModel.showListStory(user.token)
            mainViewModel.itemStory.observe(this) {
                adapter.setListStory(it)
            }
        }
    }


}