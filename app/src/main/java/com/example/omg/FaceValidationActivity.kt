package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FaceValidationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_validation)

        val btnRegresar = findViewById<Button>(R.id.btnRegresarInicio)
        btnRegresar.setOnClickListener {
            // Regresar al HomeActivity (o MainActivity dependiendo de tu flujo principal)
            val intent = Intent(this, HomeActivity::class.java)
            // Limpiamos el stack para que no pueda volver atrás al formulario
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
