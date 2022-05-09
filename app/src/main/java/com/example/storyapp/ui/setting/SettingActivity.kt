package com.example.storyapp.ui.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.databinding.ActivitySettingBinding
import com.example.storyapp.ui.ViewModelUserFactory
import com.example.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_pref")

class SettingActivity : AppCompatActivity() {
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.setting_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        settingViewModel = ViewModelProvider(
            this,
            ViewModelUserFactory(LoginPreference.getInstance(dataStore))
        )[SettingViewModel::class.java]

        lifecycleScope.launchWhenCreated {
            launch {
                settingViewModel.getUser().collect {
                    if (!it.isLoggedIn) {
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
        binding.logoutButton.setOnClickListener {
            settingViewModel.logout()
            finish()
        }
        binding.changeLanguageButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            finish()
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
}

