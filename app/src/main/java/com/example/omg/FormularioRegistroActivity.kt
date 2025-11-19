package com.example.omg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

        // Vincular vistas
        nombreEditText = findViewById(R.id.nombreEditText)
        dniEditText = findViewById(R.id.dniEditText)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
        correoEditText = findViewById(R.id.correoEditText)
        btnSiguiente = findViewById(R.id.btnSiguiente)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000") // localhost para emulador
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Acción del botón
        btnSiguiente.setOnClickListener {
            val postulante = Postulante(
                nombre = nombreEditText.text.toString(),
                dni = dniEditText.text.toString(),
                fecha_nacimiento = fechaNacimientoEditText.text.toString(),
                correo = correoEditText.text.toString()
            )

            apiService.enviarDatos(postulante).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Toast.makeText(this@FormularioRegistroActivity, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@FormularioRegistroActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
