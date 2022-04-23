package com.example.storyapp.view.signUp

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
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.utils.ApiCallbackString


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
                    signupViewModel.register(name, email, password, object : ApiCallbackString {
                        override fun onResponse(success: Boolean, message: String) {
                            showAlertDialog(success, message)
                        }
                    })

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
                setMessage(getString(R.string.message_welcome_alert))
                setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                    finish()
                }
                create()
                show()
            }
        } else {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.title_welcome_elert_failed))
                setMessage(getString(R.string.register_failed) + ", $message")
                setPositiveButton(getString(R.string.next_alert)) { _, _ ->
                    binding.signUpProgressBar.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                    binding.titleTextView.visibility = View.VISIBLE
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

    private fun showLoading() {
        signupViewModel.isLoading.observe(this) {
            binding.apply {
                if (it) {
                    signUpProgressBar.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                    titleTextView.visibility = View.GONE
                    emailTextView.visibility = View.GONE
                    emailEditText.visibility = View.GONE
                    passwordTextView.visibility = View.GONE
                    passwordEditText.visibility = View.GONE
                    signUpButton.visibility = View.GONE
                } else {
                    signUpProgressBar.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    titleTextView.visibility = View.VISIBLE
                    emailTextView.visibility = View.VISIBLE
                    emailEditText.visibility = View.VISIBLE
                    passwordTextView.visibility = View.VISIBLE
                    passwordEditText.visibility = View.VISIBLE
                    signUpButton.visibility = View.VISIBLE
                }
            }
        }
    }
}