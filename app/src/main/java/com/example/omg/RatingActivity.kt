package com.example.omg

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RatingActivity : AppCompatActivity() {

    private lateinit var ivProviderPhoto: ImageView
    private lateinit var tvProviderName: TextView
    private lateinit var tvServiceName: TextView
    private lateinit var star1: ImageView
    private lateinit var star2: ImageView
    private lateinit var star3: ImageView
    private lateinit var star4: ImageView
    private lateinit var star5: ImageView
    private lateinit var etComment: EditText
    private lateinit var btnSubmit: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val stars = mutableListOf<ImageView>()
    private var selectedRating = 0

    private var serviceId = ""
    private var providerId = ""
    private var providerName = ""
    private var providerPhoto = ""
    private var serviceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Obtener datos del intent
        getIntentData()

        // Inicializar vistas
        initViews()

        // Configurar estrellas
        setupStars()

        // Mostrar datos del proveedor
        displayProviderInfo()

        // Configurar botón
        setupSubmitButton()
    }

    private fun getIntentData() {
        serviceId = intent.getStringExtra("SERVICE_ID") ?: ""
        providerId = intent.getStringExtra("PROVIDER_ID") ?: ""
        providerName = intent.getStringExtra("PROVIDER_NAME") ?: "Proveedor"
        providerPhoto = intent.getStringExtra("PROVIDER_PHOTO") ?: ""
        serviceName = intent.getStringExtra("SERVICE_NAME") ?: "Servicio"
    }

    private fun initViews() {
        ivProviderPhoto = findViewById(R.id.ivProviderPhoto)
        tvProviderName = findViewById(R.id.tvProviderName)
        tvServiceName = findViewById(R.id.tvServiceName)
        star1 = findViewById(R.id.star1)
        star2 = findViewById(R.id.star2)
        star3 = findViewById(R.id.star3)
        star4 = findViewById(R.id.star4)
        star5 = findViewById(R.id.star5)
        etComment = findViewById(R.id.etComment)
        btnSubmit = findViewById(R.id.btnSubmit)

        stars.addAll(listOf(star1, star2, star3, star4, star5))
    }

    private fun setupStars() {
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                selectRating(index + 1)
            }
        }
    }

    private fun selectRating(rating: Int) {
        selectedRating = rating

        // Actualizar estrellas visualmente
        stars.forEachIndexed { index, star ->
            if (index < rating) {
                star.setImageResource(R.drawable.ic_star_filled)
            } else {
                star.setImageResource(R.drawable.ic_star_outline)
            }
        }

        // Habilitar botón si hay rating
        btnSubmit.isEnabled = true
        btnSubmit.alpha = 1f
    }

    private fun displayProviderInfo() {
        tvProviderName.text = providerName
        tvServiceName.text = serviceName

        // Cargar foto con Glide
        if (providerPhoto.isNotEmpty()) {
            Glide.with(this)
                .load(providerPhoto)
                .placeholder(R.drawable.ic_user)
                .into(ivProviderPhoto)
        }
    }

    private fun setupSubmitButton() {
        btnSubmit.setOnClickListener {
            if (selectedRating == 0) {
                Toast.makeText(this, "Selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitRating()
        }
    }

    private fun submitRating() {
        val userId = auth.currentUser?.uid ?: return
        val comment = etComment.text.toString().trim()

        // Deshabilitar botón mientras se guarda
        btnSubmit.isEnabled = false
        btnSubmit.text = "Guardando..."

        // Crear objeto de calificación
        val rating = hashMapOf(
            "userId" to userId,
            "providerId" to providerId,
            "serviceId" to serviceId,
            "rating" to selectedRating,
            "comment" to comment,
            "timestamp" to System.currentTimeMillis()
        )

        // Guardar en Firestore
        db.collection("ratings")
            .add(rating)
            .addOnSuccessListener {
                // Actualizar el rating promedio del proveedor
                updateProviderRating()

                Toast.makeText(
                    this,
                    "¡Gracias por tu calificación!",
                    Toast.LENGTH_SHORT
                ).show()

                // Volver atrás
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al guardar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                btnSubmit.isEnabled = true
                btnSubmit.text = "LISTO"
            }
    }

    private fun updateProviderRating() {
        // Calcular el nuevo rating promedio del proveedor
        db.collection("ratings")
            .whereEqualTo("providerId", providerId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var totalRating = 0f
                    var count = 0

                    for (document in documents) {
                        val rating = document.getLong("rating")?.toFloat() ?: 0f
                        totalRating += rating
                        count++
                    }

                    val averageRating = totalRating / count

                    // Actualizar en el proveedor
                    db.collection("providers").document(providerId)
                        .update(
                            mapOf(
                                "rating" to averageRating,
                                "reviewCount" to count
                            )
                        )
                }
            }
    }

    override fun onBackPressed() {
        // Confirmar si quiere salir sin calificar
        if (selectedRating == 0) {
            Toast.makeText(
                this,
                "¿Seguro que quieres salir sin calificar?",
                Toast.LENGTH_SHORT
            ).show()
        }
        super.onBackPressed()
    }
}