package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var iconLeft: ImageView
    private lateinit var iconRight: ImageView
    private lateinit var navHome: ImageView
    private lateinit var navProfile: ImageView
    private lateinit var navNotifications: ImageView
    private lateinit var animatedCirclesView: AnimatedCirclesView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()

        // Cargar servicios favoritos del usuario
        loadFavoriteServices()

        // Mostrar bienvenida
        val user = auth.currentUser
        Toast.makeText(
            this,
            "Bienvenido ${user?.displayName ?: user?.phoneNumber ?: "Usuario"}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initViews() {
        searchBar = findViewById(R.id.searchBar)
        iconLeft = findViewById(R.id.iconLeft)
        iconRight = findViewById(R.id.iconRight)
        navHome = findViewById(R.id.navHome)
        navProfile = findViewById(R.id.navProfile)
        navNotifications = findViewById(R.id.navNotifications)
        animatedCirclesView = findViewById(R.id.animatedCirclesView)
    }

    private fun setupListeners() {
        // ❌ QUITAR ESTO (comentado por si lo necesitas después)
        // iconLeft.setOnClickListener {
        //     openSearch()
        // }

        // ✅ SOLO la BARRA abre búsqueda
        searchBar.setOnClickListener {
            openSearch()
        }

        // Deshabilitar edición directa en la barra
        searchBar.isFocusable = false
        searchBar.isFocusableInTouchMode = false
        searchBar.isClickable = true

        // El ícono izquierdo (iconLeft) queda libre para otra función
        iconLeft.setOnClickListener {
            Toast.makeText(this, "Función pendiente", Toast.LENGTH_SHORT).show()
            // TODO: Aquí pondrás tu nueva función
        }

        iconRight.setOnClickListener {
            Toast.makeText(this, "Actualizar", Toast.LENGTH_SHORT).show()
            // TODO: Implementar actualización
        }

        navHome.setOnClickListener {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            // Ya estamos en Home
        }

        navProfile.setOnClickListener {
            // Navegar a perfil
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        navNotifications.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    // Cargar servicios favoritos desde Firestore
    private fun loadFavoriteServices() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val favoriteServices = document.get("favoriteServices") as? List<String>

                    if (!favoriteServices.isNullOrEmpty()) {
                        // Pasar los servicios a la vista de las bolas
                        animatedCirclesView.setFavoriteServices(favoriteServices)
                        Log.d("HomeActivity", "Servicios cargados: $favoriteServices")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Error al cargar servicios: ${e.message}")
            }
    }

    override fun onBackPressed() {
        // Preguntar si quiere cerrar sesión
        super.onBackPressed()
        finishAffinity() // Cerrar todas las actividades
    }
}