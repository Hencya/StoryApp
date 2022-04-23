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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.adapter.ListStoryAdapter
import com.example.storyapp.ui.setting.SettingActivity
import com.example.storyapp.ui.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("login_pref")

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var user: UserModel

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: ListStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = getString(R.string.main_title)

        setupRecycleView()
        adapter = ListStoryAdapter()

        setupViewModel()
        showLoading()
        setupAction()
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

            mainViewModel.isHaveData.observe(this) {
                binding?.apply {
                    if (it) {
                        rvStories.visibility = View.VISIBLE
                        tvInfo.visibility = View.GONE
                    } else {
                        rvStories.visibility = View.GONE
                        tvInfo.visibility = View.VISIBLE
                    }
                }
            }

            mainViewModel.showListStory(user.token)
            mainViewModel.itemStory.observe(this) {
                adapter.setListStory(it)
            }

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
    }


    private fun setupAction() {
//        binding.logoutButton.setOnClickListener {
//            mainViewModel.logout()
//        }
    }

    private fun setupRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        binding?.rvStories?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding?.rvStories?.addItemDecoration(itemDecoration)
        binding?.rvStories?.setHasFixedSize(true)
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
}