package com.example.storyapp.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.utils.ApiCallbackString
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "USER_PREF")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        showLoading()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(LoginPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailEditText.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.passwordEditText.error = getString(R.string.empty_password)
                }
                else -> {
                    loginViewModel.login(email, password, object : ApiCallbackString {
                        override fun onResponse(success: Boolean, message: String) {
                            showAlertDialog(success, message)
                        }
                    })

                }
            }


        }
    }


    private fun showAlertDialog(success: Boolean, message: String) {
        if (success) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_welcome_elert))
                setMessage(getString(R.string.message_welcome_alert))
                setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_welcome_elert_failed))
                setMessage(getString(R.string.login_failed) + ", $message")
                setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                    binding.loginProgressBar.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                    binding.titleTextView.visibility = View.VISIBLE
                    binding.messageTextView.visibility = View.VISIBLE
                    binding.emailTextView.visibility = View.VISIBLE
                    binding.emailEditText.visibility = View.VISIBLE
                    binding.passwordTextView.visibility = View.VISIBLE
                    binding.passwordEditText.visibility = View.VISIBLE
                    binding.loginButton.visibility = View.VISIBLE
                }
                create()
                show()
            }
        }
    }

    private fun showLoading() {
        loginViewModel.isLoading.observe(this) {
            binding.apply {
                if (it) {
                    loginProgressBar.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    titleTextView.visibility = View.GONE
                    messageTextView.visibility = View.GONE
                    emailTextView.visibility = View.GONE
                    emailEditText.visibility = View.GONE
                    passwordTextView.visibility = View.GONE
                    passwordEditText.visibility = View.GONE
                    loginButton.visibility = View.GONE
                } else {
                    loginProgressBar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    titleTextView.visibility = View.VISIBLE
                    messageTextView.visibility = View.VISIBLE
                    emailTextView.visibility = View.VISIBLE
                    emailEditText.visibility = View.VISIBLE
                    passwordTextView.visibility = View.VISIBLE
                    passwordEditText.visibility = View.VISIBLE
                    loginButton.visibility = View.VISIBLE
                }
            }
        }
    }

}


