package com.example.storyapp.ui.signUp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.example.storyapp.R
import com.example.storyapp.data.ResultResponse
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.ui.welcome.WelcomeActivity


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val signupViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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
        val nameTV = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameET =
            ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(500)
        val emailTV = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailET =
            ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passwordTV =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordET =
            ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signUpButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTV,
                nameET,
                emailTV,
                emailET,
                passwordTV,
                passwordET,
                signup
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
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
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
        binding.signUpButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
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
                    signupViewModel.register(name, email, password).observe(this) {
                        when (it) {
                            is ResultResponse.Loading -> {
                                binding.signUpProgressBar.visibility = View.VISIBLE
                                binding.imageView.visibility = View.GONE
                                binding.titleTextView.visibility = View.GONE
                                binding.nameTextView.visibility = View.GONE
                                binding.nameEditText.visibility = View.GONE
                                binding.emailTextView.visibility = View.GONE
                                binding.emailEditText.visibility = View.GONE
                                binding.passwordTextView.visibility = View.GONE
                                binding.passwordEditText.visibility = View.GONE
                                binding.signUpButton.visibility = View.GONE
                            }
                            is ResultResponse.Success -> {
                                binding.signUpProgressBar.visibility = View.GONE
                                showAlertDialog(true, getString(R.string.sign_up_success))
                            }
                            is ResultResponse.Error -> {
                                binding.signUpProgressBar.visibility = View.GONE
                                showAlertDialog(false, it.error)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun setMyButtonEnable() {
        val Name = binding.nameEditText.text
        val Email = binding.emailEditText.text
        val Pass = binding.passwordEditText.text

        binding.signUpButton.isEnabled =
            Name != null && Pass != null && Email != null && Name.toString()
                .isNotEmpty() && Email.toString().isNotEmpty() && Pass.toString()
                .isNotEmpty()
    }

    private fun showAlertDialog(success: Boolean, message: String) {
        if (success) {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_welcome_elert))
                setMessage(getString(R.string.message_welcome_signup_alert))
                setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                    val intent = Intent(context, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_welcome_register_alert_failed))
                setMessage(getString(R.string.register_failed) + ", $message")
                setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                    binding.signUpProgressBar.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                    binding.titleTextView.visibility = View.VISIBLE
                    binding.nameTextView.visibility = View.VISIBLE
                    binding.nameEditText.visibility = View.VISIBLE
                    binding.emailTextView.visibility = View.VISIBLE
                    binding.emailEditText.visibility = View.VISIBLE
                    binding.passwordTextView.visibility = View.VISIBLE
                    binding.passwordEditText.visibility = View.VISIBLE
                    binding.signUpButton.visibility = View.VISIBLE
                }
                create()
                show()
            }
        }
    }

}