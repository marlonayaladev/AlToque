package com.example.omg

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import android.widget.ImageView


class ProviderDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var btnBack: ImageButton
    private lateinit var ivProviderPhoto: ImageView
    private lateinit var tvProviderName: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvReviews: TextView
    private lateinit var tvArrivalTime: TextView
    private lateinit var tvExperience: TextView
    private lateinit var tvAveragePrice: TextView
    private lateinit var tvServices: TextView
    private lateinit var btnViewDetails: View
    private lateinit var detailsContainer: LinearLayout
    private lateinit var btnViewPortfolio: View
    private lateinit var btnCall: View
    private lateinit var btnMessage: View
    private lateinit var btnBackToList: View

    private var providerLat: Double = 0.0
    private var providerLng: Double = 0.0
    private var userLat: Double = 0.0
    private var userLng: Double = 0.0
    private var providerPhone: String = ""
    private var isDetailsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_provider_detail)

        // Obtener datos del intent
        getIntentData()

        // Inicializar vistas
        initViews()

        // Configurar mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar listeners
        setupListeners()

        // Llenar datos
        fillProviderData()
    }

    private fun getIntentData() {
        providerLat = intent.getDoubleExtra("PROVIDER_LAT", 0.0)
        providerLng = intent.getDoubleExtra("PROVIDER_LNG", 0.0)
        userLat = intent.getDoubleExtra("USER_LAT", 0.0)
        userLng = intent.getDoubleExtra("USER_LNG", 0.0)
        providerPhone = intent.getStringExtra("PROVIDER_PHONE") ?: ""
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        ivProviderPhoto = findViewById(R.id.ivProviderPhoto)
        tvProviderName = findViewById(R.id.tvProviderName)
        tvRating = findViewById(R.id.tvRating)
        tvReviews = findViewById(R.id.tvReviews)
        tvArrivalTime = findViewById(R.id.tvArrivalTime)
        tvExperience = findViewById(R.id.tvExperience)
        tvAveragePrice = findViewById(R.id.tvAveragePrice)
        tvServices = findViewById(R.id.tvServices)
        btnViewDetails = findViewById(R.id.btnViewDetails)
        detailsContainer = findViewById(R.id.detailsContainer)
        btnViewPortfolio = findViewById(R.id.btnViewPortfolio)
        btnCall = findViewById(R.id.btnCall)
        btnMessage = findViewById(R.id.btnMessage)
        btnBackToList = findViewById(R.id.btnBackToList)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnViewDetails.setOnClickListener {
            toggleDetails()
        }

        btnViewPortfolio.setOnClickListener {
            Toast.makeText(
                this,
                "Portafolio: Esta función se implementará próximamente",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Abrir activity de portafolio con galería de imágenes
        }

        btnCall.setOnClickListener {
            if (providerPhone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$providerPhone")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Número no disponible", Toast.LENGTH_SHORT).show()
            }
        }

        btnMessage.setOnClickListener {
            Toast.makeText(
                this,
                "Chat: Esta función se implementará próximamente",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Abrir activity de chat
        }

        btnBackToList.setOnClickListener {
            finish()
        }
    }

    private fun toggleDetails() {
        isDetailsVisible = !isDetailsVisible

        if (isDetailsVisible) {
            detailsContainer.visibility = View.VISIBLE
        } else {
            detailsContainer.visibility = View.GONE
        }
    }

    private fun fillProviderData() {
        tvProviderName.text = intent.getStringExtra("PROVIDER_NAME") ?: "Proveedor"
        tvRating.text = intent.getFloatExtra("PROVIDER_RATING", 0f).toString()
        tvReviews.text = "(${intent.getIntExtra("PROVIDER_REVIEWS", 0)})"
        tvArrivalTime.text = "5 minutos" // TODO: Calcular tiempo real
        tvExperience.text = intent.getStringExtra("PROVIDER_EXPERIENCE") ?: "N/A"
        tvAveragePrice.text = intent.getStringExtra("PROVIDER_PRICE") ?: "Consultar"

        // Servicios (simulado)
        tvServices.text = "• Reparación de grifos\n• Instalación de tuberías\n• Destapado de desagüe\n• Mantenimiento preventivo"

        // Cargar foto
        val photoUrl = intent.getStringExtra("PROVIDER_PHOTO")
        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_user)
                .into(ivProviderPhoto)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val userLocation = LatLng(userLat, userLng)
        val providerLocation = LatLng(providerLat, providerLng)

        // Marcador del usuario (rojo)
        googleMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title("Tu ubicación")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        // Marcador del proveedor (verde)
        googleMap.addMarker(
            MarkerOptions()
                .position(providerLocation)
                .title(intent.getStringExtra("PROVIDER_NAME"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )

        // Línea entre usuario y proveedor
        googleMap.addPolyline(
            PolylineOptions()
                .add(userLocation, providerLocation)
                .color(0xFF00BCD4.toInt())
                .width(8f)
        )

        // Ajustar cámara para mostrar ambos puntos
        val bounds = LatLngBounds.Builder()
            .include(userLocation)
            .include(providerLocation)
            .build()

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
    }
}