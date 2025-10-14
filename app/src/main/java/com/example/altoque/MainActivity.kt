package com.example.altoque

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore // 1. Importación necesaria

class MainActivity : AppCompatActivity() {

    // 2. Variable para almacenar la instancia de la base de datos de Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 3. Inicialización: Obtiene la instancia de Firebase Firestore
        // Este paso conecta tu app al proyecto configurado en Google Services.
        db = FirebaseFirestore.getInstance()

        // *** ¡AQUÍ PUEDES EMPEZAR A USAR 'db'! ***
        // Ejemplo (comentado) de cómo acceder a una colección:
        // val serviciosCollection = db.collection("servicios")
    }
}
