package com.app.sbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    private lateinit var edUsuarioLogin: TextInputEditText
    private lateinit var edClaveLogin: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = Firebase.auth

        edUsuarioLogin = findViewById(R.id.idUsuarioLogin)
        edClaveLogin = findViewById(R.id.idClaveLogin)

        // No tienes una cuenta(-> SignupActivity)
        val btnNoTienesCuenta = findViewById<TextView>(R.id.idNoTienesCuenta);
        btnNoTienesCuenta.setOnClickListener {
            nextActivitySignup()
        }

        // Iniciar Sesion(-> MainActivity)
        val btnIniciarSesion: Button = findViewById(R.id.btnIniciarSesion)
        btnIniciarSesion.setOnClickListener {
            val email = edUsuarioLogin.text.toString()
            val password = edClaveLogin.text.toString()
            when {
                password.isEmpty() || email.isEmpty() -> {
                    Toast.makeText(this, "Los datos ingresados están vacios o no son validos", Toast.LENGTH_LONG).show()
                } else -> {
                    signIn(email, password)
                }
            }
        }
    }

    private fun signIn(email: String, password: String){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful){
                val user = firebaseAuth.currentUser
                Toast.makeText(baseContext, "Inicio de sesion exitoso.", Toast.LENGTH_LONG).show()
                nextActivityMain()
            } else {
                Toast.makeText(baseContext, "Error de Correo y/o Contrasenia!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun nextActivityMain() {
        val nextActivity = Intent(this, MainActivity::class.java)
        this.startActivity(nextActivity)
    }

    private fun nextActivitySignup() {
        val nextActivity = Intent(this, SignupActivity::class.java);
        this.startActivity(nextActivity);
    }

    override fun onBackPressed() {
        return
    }
}