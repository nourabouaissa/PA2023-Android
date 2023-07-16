package com.example.pa_appli.view

import android.content.Context
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
import com.example.pa_appli.R
import com.example.pa_appli.databinding.ActivityLoginBinding
import com.example.pa_appli.services.LoginViewModelFactory
import com.example.pa_appli.services.SignUpViewModelFactory
import com.example.pa_appli.utils.startActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                //stocker les données de l'utilisateur avec shared preference or data store
                val sharedPref = this?.getSharedPreferences("user_data",Context.MODE_PRIVATE)
                with (sharedPref?.edit()) {
                    this?.putString(getString(R.string.user_token), loginResult.success.token)
                    this?.putString(getString(R.string.user_id), loginResult.success.userId)
                    this?.apply()
                }
                startActivity(ActivityPlayer::class.java)
                finish()
            }

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->{
                        lifecycleScope.launch {
                            loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                            )
                        }
                    }

                }
                false
            }
            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                lifecycleScope.launch {
                    loginViewModel.login(username.text.toString(), password.text.toString())
                }

            }
        }
    }


    private fun showLoginFailed( errorString: String) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */

