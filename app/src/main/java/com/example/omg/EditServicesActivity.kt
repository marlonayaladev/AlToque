package com.example.omg

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditServicesActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
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

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_services)
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        loadCurrentServices()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        chipGroup = findViewById(R.id.chipGroup)
        counterText = findViewById(R.id.counterText)
        continueButton = findViewById(R.id.continueButton)
    }

    private fun loadCurrentServices() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteServices = document.get("favoriteServices") as? List<String>

                    if (!favoriteServices.isNullOrEmpty()) {
                        selectedServices.addAll(favoriteServices)
                    }

                    // Crear chips después de cargar servicios actuales
                    createServiceChips()
                    updateCounter()
                }
            }
            .addOnFailureListener {
                // Si falla, crear chips de todas formas
                createServiceChips()
                updateCounter()
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

                // Marcar como seleccionado si ya estaba en favoritos
                isChecked = selectedServices.contains(service)

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

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        continueButton.setOnClickListener {
            if (selectedServices.size == maxSelection) {
                saveServicesAndFinish()
            } else {
                Toast.makeText(
                    this,
                    "Debes seleccionar exactamente $maxSelection servicios",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveServicesAndFinish() {
        val userId = auth.currentUser?.uid ?: return

        // Mostrar loading
        continueButton.isEnabled = false
        Toast.makeText(this, "Guardando servicios...", Toast.LENGTH_SHORT).show()

        // Guardar en Firestore
        db.collection("users").document(userId)
            .update("favoriteServices", selectedServices)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "¡Servicios actualizados correctamente!",
                    Toast.LENGTH_SHORT
                ).show()

                // Volver al perfil
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
}