package com.example.nclicker30

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class PantallaCarga : Activity() {

    private val LOADING_TIME = 2000L // 2 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_carga)

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, LOADING_TIME)
    }
}