package com.example.pa_appli.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.pa_appli.databinding.ActivitySignupBinding
import com.example.pa_appli.services.SignUpViewModelFactory
import com.example.pa_appli.utils.startActivity
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = binding.usernameEditText
        val password = binding.passwordEditText
        val btnSignup = binding.signup
        val profileImageUrl = binding.profilImageUrl
        val firstName = binding.firstName
        val lastName = binding.lastName
        val email = binding.email
        val confirmPassword = binding.passwordConfirmation
        val loading = binding.loading

        signUpViewModel =
            ViewModelProvider(this, SignUpViewModelFactory())[SignUpViewModel::class.java]

        signUpViewModel.signUpFormState.observe(this@SignupActivity, Observer {
            val signupFormState = it ?: return@Observer

            // disable login button unless both username / password is valid
            btnSignup.isEnabled = signupFormState.isDataValid

            if (signupFormState.usernameError != null) {
                username.error = getString(signupFormState.usernameError)
            }
            if (signupFormState.passwordError != null) {
                password.error = getString(signupFormState.passwordError)
            }
            if (signupFormState.firstNameError != null) {
                firstName.error = getString(signupFormState.firstNameError)
            }
            if (signupFormState.lastNameError != null) {
                lastName.error = getString(signupFormState.lastNameError)
            }
            if (signupFormState.confirmationPasswordError != null) {
                confirmPassword.error = getString(signupFormState.confirmationPasswordError)
            }
            if (signupFormState.profileImageUrlError != null) {
                profileImageUrl.error = getString(signupFormState.profileImageUrlError)
            } else {
                profileImageUrl.error = null
            }
            if (signupFormState.emailError != null) {
                email.error = getString(signupFormState.emailError)
            }
        })

        signUpViewModel.signUpResult.observe(this@SignupActivity, Observer {
            val signUpResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (signUpResult.error != null) {
                showLoginFailed(signUpResult.error)
            }
            if (signUpResult.success != null) {
                //stocker les données de l'utilisateur avec shared preference or data store
                startActivity(LoginActivity::class.java)
                finish()
            }

        })

        username.afterTextChanged {
            lifecycleScope.launch {
                signUpViewModel.loginDataChanged(
                    username.text.toString(),
                    firstName.text.toString(),
                    lastName.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString(),
                    profileImageUrl.editText?.text.toString(),
                    email.text.toString()
                )
            }

        }
        profileImageUrl.editText?.afterTextChanged {
            signUpViewModel.loginDataChanged(
                username.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString(),
                profileImageUrl.editText?.text.toString(),
                email.text.toString()
            )
        }
        firstName.afterTextChanged {
            signUpViewModel.loginDataChanged(
                username.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString(),
                profileImageUrl.editText?.text.toString(),
                email.text.toString()
            )
        }
        lastName.afterTextChanged {
            signUpViewModel.loginDataChanged(
                username.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString(),
                profileImageUrl.editText?.text.toString(),
                email.text.toString()
            )
        }
        email.afterTextChanged {
            signUpViewModel.loginDataChanged(
                username.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString(),
                profileImageUrl.editText?.text.toString(),
                email.text.toString()
            )
        }
        confirmPassword.afterTextChanged {
            signUpViewModel.loginDataChanged(
                username.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                password.text.toString(),
                confirmPassword.text.toString(),
                profileImageUrl.editText?.text.toString(),
                email.text.toString()
            )
        }
        password.apply {
            afterTextChanged {
                signUpViewModel.loginDataChanged(
                    username.text.toString(),
                    firstName.text.toString(),
                    lastName.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString(),
                    profileImageUrl.editText?.text.toString(),
                    email.text.toString()
                )
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        lifecycleScope.launch {
                            signUpViewModel.signUp(
                                username.text.toString(),
                                firstName.text.toString(),
                                lastName.text.toString(),
                                password.text.toString(),
                                confirmPassword.text.toString(),
                                profileImageUrl.editText?.text.toString(),
                                email.text.toString()

                            )
                        }
                    }

                }
                false
            }
            btnSignup.setOnClickListener {
                loading.visibility = View.VISIBLE
                lifecycleScope.launch {
                    signUpViewModel.signUp(
                        username.text.toString(),
                        firstName.text.toString(),
                        lastName.text.toString(),
                        password.text.toString(),
                        confirmPassword.text.toString(),
                        profileImageUrl.editText?.text.toString(),
                        email.text.toString()

                    )
                }

            }
        }
    }


    private fun showLoginFailed(errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

