package com.example.submissionstoryapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.ViewModelFactory
import com.example.submissionstoryapp.databinding.ActivitySignupBinding
import com.example.submissionstoryapp.data.Result
import com.example.submissionstoryapp.databinding.CustomActionbarBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupAction()

        supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            customView = CustomActionbarBinding.inflate(layoutInflater).root
        }
    }

    private fun playAnimation() {
        val animations = listOf(
            binding.titleTextView, binding.nameTextView, binding.nameEditTextLayout,
            binding.emailTextView, binding.emailEditTextLayout, binding.passwordTextView,
            binding.passwordEditTextLayout, binding.signupButton
        ).map {
            ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        }

        AnimatorSet().apply {
            playSequentially(animations)
            start()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text?.toString() ?: ""
            val email = binding.emailEditText.text?.toString() ?: ""
            val password = binding.passwordEditText.text?.toString() ?: ""

            if (isValidInput(name, email, password)) {
                viewModel.register(name, email, password)

                viewModel.registrationResult.observe(this) { result ->
                    showLoading(result is Result.Loading)

                    when (result) {
                        is Result.Success -> {
                            showLoading(false)
                            result.data.let { data ->
                                data.message?.let { message -> showSuccessDialog(message) }
                            }
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showErrorDialog(result.error)
                        }

                        is Result.Loading -> showLoading(true)
                    }
                }
            } else {
                // Handle invalid input, show a toast, or provide feedback to the user
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidInput(name: String, email: String, password: String): Boolean {
        return name.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches() && password.length >= MIN_PASSWORD_LENGTH
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessDialog(message: String) {
        showDialog(getString(R.string.success_title), message) { finish() }
    }

    private fun showErrorDialog(errorMessage: String) {
        showDialog(
            getString(R.string.error_title),
            errorMessage
        ) { /* You can add custom handling here if needed */ }
    }

    private fun showDialog(title: String, message: String, positiveAction: () -> Unit) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { _, _ -> positiveAction.invoke() }
            show()
        }
    }

    private companion object {
        const val ANIMATION_DURATION = 100L
        const val MIN_PASSWORD_LENGTH = 8
    }
}
