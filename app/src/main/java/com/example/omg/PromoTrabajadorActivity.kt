package com.example.omg

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class PromoTrabajadorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_trabajador)

        val btnConvertirse = findViewById<Button>(R.id.btnConvertirse)
        btnConvertirse.setOnClickListener {
            val intent = Intent(this, FormularioRegistroActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)

        btnConvertirse.setOnClickListener {
            val intent = Intent(this, FaceValidationActivity::class.java)
            startActivity(intent)
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }
}
