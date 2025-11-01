package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class ProvidersListActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var btnBack: ImageButton
    private lateinit var tvServiceName: TextView
    private lateinit var tvProvidersCount: TextView
    private lateinit var rvProviders: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var db: FirebaseFirestore
    private var serviceName: String = ""
    private var userLat: Double = 0.0
    private var userLng: Double = 0.0
    private val providersList = mutableListOf<Provider>()
    private lateinit var providersAdapter: ProvidersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_providers_list)

        // Obtener datos del intent
        serviceName = intent.getStringExtra("SERVICE_NAME") ?: "Servicio"
        userLat = intent.getDoubleExtra("LATITUDE", -12.0931)
        userLng = intent.getDoubleExtra("LONGITUDE", -77.0465)

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance()

        // Inicializar vistas
        initViews()

        // Configurar RecyclerView
        setupRecyclerView()

        // Configurar mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar listeners
        setupListeners()

        // Cargar proveedores
        loadProviders()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvServiceName = findViewById(R.id.tvServiceName)
        tvProvidersCount = findViewById(R.id.tvProvidersCount)
        rvProviders = findViewById(R.id.rvProviders)
        progressBar = findViewById(R.id.progressBar)

        tvServiceName.text = serviceName
    }

    private fun setupRecyclerView() {
        providersAdapter = ProvidersAdapter(providersList) { provider ->
            // Click en un proveedor
            val intent = Intent(this, ProviderDetailActivity::class.java).apply {
                putExtra("PROVIDER_ID", provider.id)
                putExtra("PROVIDER_NAME", provider.name)
                putExtra("PROVIDER_PHOTO", provider.photoUrl)
                putExtra("PROVIDER_RATING", provider.rating)
                putExtra("PROVIDER_REVIEWS", provider.reviewCount)
                putExtra("PROVIDER_SERVICE", provider.service)
                putExtra("PROVIDER_PRICE", provider.averagePrice)
                putExtra("PROVIDER_EXPERIENCE", provider.experience)
                putExtra("PROVIDER_DESCRIPTION", provider.description)
                putExtra("PROVIDER_LAT", provider.latitude)
                putExtra("PROVIDER_LNG", provider.longitude)
                putExtra("PROVIDER_PHONE", provider.phone)
                putExtra("USER_LAT", userLat)
                putExtra("USER_LNG", userLng)
            }
            startActivity(intent)
        }

        rvProviders.apply {
            layoutManager = LinearLayoutManager(this@ProvidersListActivity)
            adapter = providersAdapter
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Ubicación del usuario
        val userLocation = LatLng(userLat, userLng)

        // Agregar marcador del usuario
        googleMap.addMarker(
            MarkerOptions()
                .position(userLocation)
                .title("Tu ubicación")
        )

        // Mover cámara
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))

        // ✅ AHORA SÍ agregar los marcadores de proveedores (después de que el mapa esté listo)
        if (providersList.isNotEmpty()) {
            addProvidersToMap()
        }
    }

    private fun loadProviders() {
        progressBar.visibility = View.VISIBLE

        // 🔥 Por ahora usamos datos de prueba
        // TODO: Cargar desde Firestore cuando tengas proveedores reales

        createMockProviders()

        providersList.addAll(getMockProviders())
        providersAdapter.notifyDataSetChanged()

        tvProvidersCount.text = "${providersList.size} proveedores cercanos"
        progressBar.visibility = View.GONE

        // ✅ SOLO agregar marcadores si el mapa YA está listo
        if (::googleMap.isInitialized) {
            addProvidersToMap()
        }
    }

    private fun getMockProviders(): List<Provider> {
        return listOf(
            Provider(
                id = "1",
                name = "Aitor Tilla",
                photoUrl = "",
                service = serviceName,
                rating = 4.8f,
                reviewCount = 142,
                priceRange = "S/ 50-80",
                averagePrice = "S/ 65",
                arrivalTime = "5 minutos",
                description = "Experto en $serviceName con 10 años de experiencia",
                experience = "10 años",
                servicesOffered = listOf(
                    "Reparación de grifos",
                    "Instalación de tuberías",
                    "Destapado de desagüe",
                    "Mantenimiento de tuberías"
                ),
                latitude = userLat + 0.005,
                longitude = userLng + 0.005,
                phone = "+51999888777",
                isAvailable = true
            ),
            Provider(
                id = "2",
                name = "Elsa Pato",
                photoUrl = "",
                service = serviceName,
                rating = 4.9f,
                reviewCount = 203,
                priceRange = "S/ 60-90",
                averagePrice = "S/ 75",
                arrivalTime = "8 minutos",
                description = "Profesional certificado en $serviceName",
                experience = "8 años",
                servicesOffered = listOf(
                    "Reparación de grifos",
                    "Instalación",
                    "Mantenimiento"
                ),
                latitude = userLat - 0.003,
                longitude = userLng + 0.007,
                phone = "+51999888666",
                isAvailable = true
            ),
            Provider(
                id = "3",
                name = "Armando Bronca",
                photoUrl = "",
                service = serviceName,
                rating = 4.7f,
                reviewCount = 89,
                priceRange = "S/ 45-70",
                averagePrice = "S/ 55",
                arrivalTime = "12 minutos",
                description = "Servicio rápido y económico",
                experience = "5 años",
                servicesOffered = listOf(
                    "Reparaciones generales",
                    "Instalación",
                    "Emergencias"
                ),
                latitude = userLat + 0.008,
                longitude = userLng - 0.004,
                phone = "+51999888555",
                isAvailable = true
            )
        )
    }

    private fun createMockProviders() {
        // Esta función simula crear proveedores en Firestore
        // TODO: Implementar cuando tengas el panel de administración
    }

    private fun addProvidersToMap() {
        for (provider in providersList) {
            val position = LatLng(provider.latitude, provider.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(provider.name)
                    .snippet("${provider.rating}⭐ - ${provider.arrivalTime}")
            )
        }
    }
}