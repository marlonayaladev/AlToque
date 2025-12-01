package com.example.omg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.omg.model.EstadoResponse
import com.example.omg.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromoTrabajadorActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_trabajador)

        auth = FirebaseAuth.getInstance()

        val btnConvertirse = findViewById<Button>(R.id.btnConvertirse)
        btnConvertirse.setOnClickListener {
            checkUserStatus()
        }

        val btnRegresar = findViewById<Button>(R.id.btnRegresar)
        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun checkUserStatus() {
        // 1. Intentamos obtener el email de Firebase
        var correo = auth.currentUser?.email

        // 2. Si es nulo (login telefónico), buscamos en SharedPreferences
        if (correo.isNullOrEmpty()) {
            val prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            correo = prefs.getString("correo_postulante", null)
        }
        
        // 3. Si sigue siendo nulo, no podemos verificar
        if (correo.isNullOrEmpty()) {
            // Asumimos que no se ha registrado nunca -> Formulario
            navegarSegunEstado("no_registrado")
            return
        }

        // ✅ CAMBIO: Usamos RetrofitClient
        val apiService = RetrofitClient.instance

        // Llamada al backend
        apiService.verificarEstado(correo).enqueue(object : Callback<EstadoResponse> {
            override fun onResponse(call: Call<EstadoResponse>, response: Response<EstadoResponse>) {
                if (response.isSuccessful) {
                    val estado = response.body()?.estado ?: "no_registrado"
                    navegarSegunEstado(estado)
                } else {
                    navegarSegunEstado("no_registrado")
                }
            }

            override fun onFailure(call: Call<EstadoResponse>, t: Throwable) {
                Toast.makeText(this@PromoTrabajadorActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navegarSegunEstado(estado: String) {
        val intent = when (estado) {
            "admitido" -> Intent(this, TrabajadorHomeActivity::class.java)
            "pendiente" -> Intent(this, FaceValidationActivity::class.java)
            "denegado" -> {
                Toast.makeText(this, "Tu solicitud fue denegada. Contacta soporte.", Toast.LENGTH_LONG).show()
                null
            }
            else -> Intent(this, FormularioRegistroActivity::class.java) // "no_registrado"
        }

        intent?.let {
            startActivity(it)
            if (estado == "admitido") finish() // Si ya es trabajador, cerramos la promo
        }
    }
}
