package com.example.pa_appli.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pa_appli.R

class SignupActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvError = findViewById(R.id.tvError)

        btnSignup.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (validateInput(email, password)) {
                // Appel à la fonction d'inscription
                signUp(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            tvError.text = "Veuillez remplir tous les champs."
            tvError.visibility = View.VISIBLE
            return false
        }
        return true
    }

    private fun signUp(email: String, password: String) {
        // Effectuer l'inscription de l'utilisateur ici
        // Utilisez votre système de gestion des utilisateurs ou appelez l'API appropriée
    }
}
