package com.example.omg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationConfirmationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnBack: ImageButton
    private lateinit var btnConfirm: View
    private lateinit var btnChange: View
    private lateinit var tvAddress: TextView

    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0
    private var selectedService: String = ""

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_confirmation)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        // Obtener el servicio seleccionado
        selectedService = intent.getStringExtra("SERVICE_NAME") ?: "Servicio"

        // Inicializar vistas
        initViews()

        // Inicializar ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configurar listeners
        setupListeners()

        // Solicitar permisos y obtener ubicación
        checkLocationPermission()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnConfirm = findViewById(R.id.btnConfirm)
        btnChange = findViewById(R.id.btnChange)
        tvAddress = findViewById(R.id.tvAddress)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {
            if (currentLat != 0.0 && currentLng != 0.0) {
                // Ir a la lista de proveedores
                val intent = Intent(this, ProvidersListActivity::class.java).apply {
                    putExtra("SERVICE_NAME", selectedService)
                    putExtra("LATITUDE", currentLat)
                    putExtra("LONGITUDE", currentLng)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Esperando ubicación...", Toast.LENGTH_SHORT).show()
            }
        }

        btnChange.setOnClickListener {
            finish()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            // Solicitar permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Obtener ubicación actual
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLng = location.longitude

                    // Obtener dirección
                    getAddressFromLocation(currentLat, currentLng)
                } else {
                    // Ubicación por defecto (San Isidro, Lima)
                    currentLat = -12.0931
                    currentLng = -77.0465
                    tvAddress.text = "San Isidro, Lima, Perú (Ubicación por defecto)"
                }
            }
        }
    }

    private fun getAddressFromLocation(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address>? = geocoder.getFromLocation(
                lat,
                lng,
                1
            )

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressText = buildString {
                    if (address.thoroughfare != null) append(address.thoroughfare)
                    if (address.subLocality != null) {
                        if (isNotEmpty()) append(", ")
                        append(address.subLocality)
                    }
                    if (address.locality != null) {
                        if (isNotEmpty()) append(", ")
                        append(address.locality)
                    }
                }
                tvAddress.text = addressText.ifEmpty { "Ubicación actual" }
            } else {
                tvAddress.text = "Ubicación actual"
            }
        } catch (e: Exception) {
            tvAddress.text = "Ubicación actual"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                Toast.makeText(
                    this,
                    "Necesitamos permisos de ubicación para continuar",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
}