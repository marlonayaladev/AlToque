package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout  // ✅ AGREGADO
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class SearchActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var rvResults: RecyclerView
    private lateinit var tvNoResults: LinearLayout
    private lateinit var tvRecentSearches: TextView

    private lateinit var auth: FirebaseAuth
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

    private val filteredList = mutableListOf<String>()
    private lateinit var searchAdapter: SearchAdapter

    // Búsquedas recientes (simulado, podrías guardar en SharedPreferences)
    private val recentSearches = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        auth = FirebaseAuth.getInstance()

        initViews()
        setupRecyclerView()
        setupListeners()

        // Mostrar todos los servicios al inicio
        showAllServices()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        etSearch = findViewById(R.id.etSearch)
        rvResults = findViewById(R.id.rvResults)
        tvNoResults = findViewById(R.id.tvNoResults)
        tvRecentSearches = findViewById(R.id.tvRecentSearches)

        // Focus automático en el campo de búsqueda
        etSearch.requestFocus()
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter(filteredList) { service ->
            // Click en un servicio
            onServiceSelected(service)
        }

        rvResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = searchAdapter
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterServices(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showAllServices() {
        filteredList.clear()
        filteredList.addAll(servicesList)
        searchAdapter.notifyDataSetChanged()

        tvNoResults.visibility = View.GONE
        rvResults.visibility = View.VISIBLE
        tvRecentSearches.text = "Todos los servicios"
    }

    private fun filterServices(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            // Si no hay búsqueda, mostrar todos
            showAllServices()
            return
        }

        // Filtrar servicios que contengan el texto buscado
        val results = servicesList.filter { service ->
            service.lowercase().contains(query.lowercase())
        }

        filteredList.addAll(results)
        searchAdapter.notifyDataSetChanged()

        // Mostrar/ocultar mensaje de "no resultados"
        if (filteredList.isEmpty()) {
            tvNoResults.visibility = View.VISIBLE
            rvResults.visibility = View.GONE
            tvRecentSearches.text = "No se encontraron resultados"
        } else {
            tvNoResults.visibility = View.GONE
            rvResults.visibility = View.VISIBLE
            tvRecentSearches.text = "${filteredList.size} resultados encontrados"
        }
    }

    private fun onServiceSelected(service: String) {
        // Agregar a búsquedas recientes
        if (!recentSearches.contains(service)) {
            recentSearches.add(0, service)
        }

        // Obtener ubicación actual (simulado)
        val userLat = -12.0931
        val userLng = -77.0465

        // Ir a la lista de proveedores de ese servicio
        val intent = Intent(this, ProvidersListActivity::class.java).apply {
            putExtra("SERVICE_NAME", service)
            putExtra("LATITUDE", userLat)
            putExtra("LONGITUDE", userLng)
        }
        startActivity(intent)
        finish()
    }
}