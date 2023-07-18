package com.app.sbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    // Duracion de 8 segundos de carga de la SplashActivity
    private val splashDuracion: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ocultar la barra de título (cabecera)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)

        // Siguiente Actividad(LoginActivity)
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, splashDuracion)
    }
}