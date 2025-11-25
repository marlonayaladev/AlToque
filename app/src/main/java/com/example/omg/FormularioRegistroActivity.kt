package com.example.omg

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.omg.model.Postulante
import com.example.omg.network.ApiService
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FormularioRegistroActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var dniEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var correoEditText: EditText
    private lateinit var btnSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_registro)

        // Referencias a los campos
        nombreEditText = findViewById(R.id.nombreEditText)
        dniEditText = findViewById(R.id.dniEditText)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
        correoEditText = findViewById(R.id.correoEditText)
        btnSiguiente = findViewById(R.id.btnSiguiente)

        // Configuración de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000") // backend local
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Acción del botón
        btnSiguiente.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val dni = dniEditText.text.toString().trim()
            val fecha = fechaNacimientoEditText.text.toString().trim()
            val correo = correoEditText.text.toString().trim()

            // Validaciones básicas
            if (nombre.isEmpty() || dni.length != 8 ||
                !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Completa los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto postulante
            val postulante = Postulante(nombre, dni, fecha, correo)

            // Enviar datos al backend
            apiService.enviarDatos(postulante).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@FormularioRegistroActivity,
                            "Datos enviados correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 👉 Aquí ya no abrimos FaceValidationActivity
                        // Solo mostramos confirmación
                    } else {
                        Toast.makeText(
                            this@FormularioRegistroActivity,
                            "Error en el servidor: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@FormularioRegistroActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
}
