package com.example.storyapp.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.ViewModelUserFactory
import com.example.storyapp.ui.adapter.ListStoryAdapter
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.maps.MapsActivity
import com.example.storyapp.ui.setting.SettingActivity
import com.example.storyapp.ui.uploadStory.UploadStoryActivity
import com.example.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("login_pref")

class MainActivity : AppCompatActivity() {
    private lateinit var user: UserModel
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var mainPrefViewModel: MainPrefViewModel
    private lateinit var adapter: ListStoryAdapter
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = getString(R.string.main_title)

        adapter = ListStoryAdapter()
        setupViewModel()

        initSwipeToRefresh()

        setupAction()
        setupRecycleView()
    }

    private fun initAdapter(user: UserModel) {
        adapter = ListStoryAdapter()
        binding?.rvStories?.adapter = adapter.withLoadStateHeaderAndFooter(
            footer = LoadingStateAdapter { adapter.retry() },
            header = LoadingStateAdapter { adapter.retry() }
        )

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collect {
                binding?.swipeRefresh?.isRefreshing = it.mediator?.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding?.viewError?.root?.isVisible = loadStates.refresh is LoadState.Error
            }
            if (adapter.itemCount < 1) binding?.viewError?.root?.visibility = View.VISIBLE
            else binding?.viewError?.root?.visibility = View.GONE
        }

        mainViewModel.getStory(user.token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun initSwipeToRefresh() {
        binding?.swipeRefresh?.setOnRefreshListener { adapter.refresh() }
    }


    private fun setupViewModel() {
        mainPrefViewModel = ViewModelProvider(
            this,
            ViewModelUserFactory(LoginPreference.getInstance(dataStore))
        )[MainPrefViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            launch {
                mainPrefViewModel.getUser().collect { user ->
                    if (user.isLoggedIn) {
                        binding?.tvName?.text = getString(R.string.hallo_user, user.name)
                        initAdapter(user)
                    } else {
                        gotoWelcomeActivity()
                    }
                }
            }
        }
    }

    private fun gotoWelcomeActivity() {
        startActivity(
            Intent(this, WelcomeActivity::class.java),
            ActivityOptionsCompat.makeSceneTransitionAnimation(this as Activity).toBundle()
        )
        finish()
    }

    private fun setupAction() {
        binding?.fabAdd?.setOnClickListener { view ->
            if (view.id == R.id.fab_add) {
                lifecycleScope.launchWhenCreated {
                    launch {
                        mainPrefViewModel.getUser().collect {
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
            R.id.menu_maps -> {
                lifecycleScope.launchWhenCreated {
                    launch {
                        mainPrefViewModel.getUser().collect {
                            user = UserModel(
                                it.userId,
                                it.name,
                                it.email,
                                it.password,
                                it.token,
                                true
                            )
                            val mapsActivityIntent =
                                Intent(this@MainActivity, MapsActivity::class.java)
                            mapsActivityIntent.putExtra(UploadStoryActivity.DATA_USER, user)
                            startActivity(mapsActivityIntent)
                        }
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenCreated {
            launch {
                mainPrefViewModel.getUser().collect { user ->
                    binding?.tvName?.text = getString(R.string.hallo_user, user.name)
                    initAdapter(user)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}