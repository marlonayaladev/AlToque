package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ServiceSelectionActivity : AppCompatActivity() {

    private lateinit var chipGroup: ChipGroup
    private lateinit var counterText: TextView
    private lateinit var continueButton: View

    private val selectedServices = mutableListOf<String>()
    private val maxSelection = 4

    private val servicesList = listOf(
        "Gasfitería",
        "Jardinería",
        "Repartidor de Gas",
        "Servicio de Limpieza",
        "Mozos",
        "Masajista",
        "Manicure",
        "Técnico",
        "Profesores",
        "Electricista",
        "Plomería",
        "Carpintería",
        "Pintura",
        "Delivery",
        "Seguridad"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_selection)

        chipGroup = findViewById(R.id.chipGroup)
        counterText = findViewById(R.id.counterText)
        continueButton = findViewById(R.id.continueButton)

        createServiceChips()
        updateCounter()

        continueButton.setOnClickListener {
            if (selectedServices.size == maxSelection) {
                saveServicesAndContinue()
            } else {
                Toast.makeText(
                    this,
                    "Debes seleccionar exactamente $maxSelection servicios",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createServiceChips() {
        for (service in servicesList) {
            val chip = Chip(this).apply {
                text = service
                isCheckable = true
                chipBackgroundColor = getColorStateList(R.color.chip_background_selector)
                setTextColor(getColorStateList(R.color.chip_text_selector))
                chipStrokeWidth = 2f
                chipStrokeColor = getColorStateList(R.color.chip_stroke_selector)

                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (selectedServices.size < maxSelection) {
                            selectedServices.add(service)
                        } else {
                            // Ya tiene 4, no permitir más
                            this.isChecked = false
                            Toast.makeText(
                                context,
                                "Solo puedes seleccionar $maxSelection servicios",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        selectedServices.remove(service)
                    }
                    updateCounter()
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun updateCounter() {
        counterText.text = "${selectedServices.size}/$maxSelection servicios seleccionados"

        // Habilitar/deshabilitar botón continuar
        continueButton.isEnabled = selectedServices.size == maxSelection
        continueButton.alpha = if (selectedServices.size == maxSelection) 1f else 0.5f
    }

    private fun saveServicesAndContinue() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        // Mostrar loading
        continueButton.isEnabled = false
        Toast.makeText(this, "Guardando servicios...", Toast.LENGTH_SHORT).show()

        // Guardar en Firestore
        db.collection("users").document(userId)
            .update("favoriteServices", selectedServices)
            .addOnSuccessListener {
                // Ir al Home
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error al guardar: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                continueButton.isEnabled = true
            }
    }

    override fun onBackPressed() {
        // No permitir volver atrás
        Toast.makeText(
            this,
            "Debes seleccionar tus servicios para continuar",
            Toast.LENGTH_SHORT
        ).show()
    }
}