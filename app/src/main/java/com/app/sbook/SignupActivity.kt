package com.app.sbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var edUsuarioSignup: TextInputEditText
    private lateinit var edClaveSignup: TextInputEditText
    private lateinit var edNombreSignup: TextInputEditText
    private lateinit var edClaveConfirmarSignup: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        edUsuarioSignup = findViewById(R.id.idUsuarioSignup)
        edClaveSignup = findViewById(R.id.idClaveSignup)
        edNombreSignup = findViewById(R.id.idNombreSignup)
        edClaveConfirmarSignup = findViewById(R.id.idClaveConfirmarSignup)

        // Tengo una cuenta?(-> LoginActivity)
        val btnTengoCuenta = findViewById<TextView>(R.id.idTengoCuenta);
        btnTengoCuenta.setOnClickListener {
            nextActivityLogin()
        }

        // Crear una cuenta?(-> LoginActivity)
        firebaseAuth = Firebase.auth
        val btnRegistrarCuenta: Button = findViewById(R.id.btnRegistrarCuenta)
        btnRegistrarCuenta.setOnClickListener {
            val email = edUsuarioSignup.text.toString()
            val password = edClaveSignup.text.toString()
            val passwordConfirm = edClaveConfirmarSignup.text.toString()
            val name = edNombreSignup.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirm.isNotEmpty() && name.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (password == passwordConfirm) {
                        crearCuentaNueva(email, password, name)
                    } else {
                        Toast.makeText(this, "¡Las contraseñas no coinciden!", Toast.LENGTH_LONG).show()
                        edClaveSignup.requestFocus()
                    }
                } else {
                    Toast.makeText(this, "¡Ingresa un correo valido!", Toast.LENGTH_LONG).show()
                    edUsuarioSignup.requestFocus()
                }
            } else {
                Toast.makeText(this, "¡Completa todos los campos!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"
        return email.matches(emailRegex.toRegex())
    }

    private fun crearCuentaNueva(email: String, password: String, name: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful) {
                        Toast.makeText(baseContext, "Se registro la cuenta exitosamente", Toast.LENGTH_LONG).show()
                        nextActivityLogin()
                    } else {
                        Toast.makeText(baseContext, "Error al crear la cuenta: " + profileTask.exception, Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(baseContext, "Error: " + task.exception, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun nextActivityLogin() {
        val nextActivity = Intent(this, LoginActivity::class.java)
        this.startActivity(nextActivity)
    }
}