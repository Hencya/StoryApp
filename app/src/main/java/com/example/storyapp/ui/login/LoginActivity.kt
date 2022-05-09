package com.example.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.data.ResultResponse
import com.example.storyapp.data.model.UserModel
import com.example.storyapp.data.preferences.LoginPreference
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.main.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_pref")

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setMyButtonEnable()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val messageTV =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTV = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailET =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passwordTV =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordET =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                messageTV,
                emailTV,
                emailET,
                passwordTV,
                passwordET,
                login
            )
            start()
        }
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

    private fun setupAction() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
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
                    loginViewModel.login(email, password).observe(this) {
                        when (it) {
                            is ResultResponse.Loading -> {
                                binding.loginProgressBar.visibility = View.VISIBLE
                                binding.imageView.visibility = View.GONE
                                binding.titleTextView.visibility = View.GONE
                                binding.messageTextView.visibility = View.GONE
                                binding.emailTextView.visibility = View.GONE
                                binding.emailEditText.visibility = View.GONE
                                binding.passwordTextView.visibility = View.GONE
                                binding.passwordEditText.visibility = View.GONE
                                binding.loginButton.visibility = View.GONE
                            }
                            is ResultResponse.Success -> {
                                binding.loginProgressBar.visibility = View.GONE
                                val user = UserModel(
                                    it.data.name,
                                    email,
                                    password,
                                    it.data.userId,
                                    it.data.token,
                                    true
                                )
                                showAlertDialog(true, getString(R.string.log_in_success))

                                val userPref = LoginPreference.getInstance(dataStore)
                                lifecycleScope.launchWhenStarted {
                                    userPref.saveUser(user)
                                }
                            }
                            is ResultResponse.Error -> {
                                binding.loginProgressBar.visibility = View.GONE
                                showAlertDialog(false, it.error)
                            }
                        }
                    }

                }
            }


        }
    }

    private fun setMyButtonEnable() {
        val Email = binding.emailEditText.text
        val Pass = binding.passwordEditText.text

        binding.loginButton.isEnabled =
            Pass != null && Email != null && Email.toString().isNotEmpty() && Pass.toString()
                .isNotEmpty()
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
                setTitle(getString(R.string.title_welcome_alert_failed))
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
}


