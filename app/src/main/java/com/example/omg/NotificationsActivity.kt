package com.example.omg

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationsActivity : AppCompatActivity() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var navHome: ImageView
    private lateinit var navProfile: ImageView
    private lateinit var navNotifications: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val notificationsList = mutableListOf<Notification>()
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupRecyclerView()
        setupNavigation()
        loadNotifications()
    }

    private fun initViews() {
        rvNotifications = findViewById(R.id.rvNotifications)
        emptyView = findViewById(R.id.emptyView)
        navHome = findViewById(R.id.navHome)
        navProfile = findViewById(R.id.navProfile)
        navNotifications = findViewById(R.id.navNotifications)
    }

    private fun setupRecyclerView() {
        notificationsAdapter = NotificationsAdapter(notificationsList) { notification ->
            handleNotificationClick(notification)
        }

        rvNotifications.apply {
            layoutManager = LinearLayoutManager(this@NotificationsActivity)
            adapter = notificationsAdapter
        }
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        navNotifications.setOnClickListener {
            // Ya estamos aquí
        }
    }

    private fun loadNotifications() {
        val userId = auth.currentUser?.uid ?: return

        // 🔥 Por ahora usamos datos de prueba
        // TODO: Cargar desde Firestore cuando tengas notificaciones reales

        createMockNotifications()

        notificationsList.addAll(getMockNotifications())
        notificationsAdapter.notifyDataSetChanged()

        // Mostrar/ocultar vista vacía
        if (notificationsList.isEmpty()) {
            rvNotifications.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            rvNotifications.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    private fun getMockNotifications(): List<Notification> {
        return listOf(
            // Notificación de publicidad
            Notification(
                id = "1",
                title = "Publicidad",
                message = "El servicio de gasfitería culminó",
                type = "ad",
                timestamp = System.currentTimeMillis() - 300000, // Hace 5 minutos
                isRead = false,
                serviceId = "service_123",
                providerId = "provider_1",
                providerName = "Aitor Tilla",
                providerPhoto = "",
                serviceName = "Gasfitería",
                adText = "¡Ahorra! Pide por Rappi"
            ),
            // Notificación normal
            Notification(
                id = "2",
                title = "Servicio completado",
                message = "Tu servicio de electricista fue completado",
                type = "service_completed",
                timestamp = System.currentTimeMillis() - 3600000, // Hace 1 hora
                isRead = false,
                serviceId = "service_124",
                providerId = "provider_2",
                providerName = "Elsa Pato",
                providerPhoto = "",
                serviceName = "Electricista"
            ),
            // Notificación antigua
            Notification(
                id = "3",
                title = "Recordatorio",
                message = "¿Olvidaste calificar a tu proveedor?",
                type = "rating_reminder",
                timestamp = System.currentTimeMillis() - 86400000, // Hace 1 día
                isRead = true,
                serviceId = "service_122",
                providerId = "provider_3",
                providerName = "Armando Bronca",
                providerPhoto = "",
                serviceName = "Plomería"
            )
        )
    }

    private fun createMockNotifications() {
        // Simula crear notificaciones
        // TODO: Implementar cuando tengas sistema de pedidos real
    }

    private fun handleNotificationClick(notification: Notification) {
        // Marcar como leída
        markAsRead(notification.id)

        // Según el tipo, abrir pantalla correspondiente
        when (notification.type) {
            "service_completed", "rating_reminder" -> {
                // Abrir pantalla de calificación
                val intent = Intent(this, RatingActivity::class.java).apply {
                    putExtra("SERVICE_ID", notification.serviceId)
                    putExtra("PROVIDER_ID", notification.providerId)
                    putExtra("PROVIDER_NAME", notification.providerName)
                    putExtra("PROVIDER_PHOTO", notification.providerPhoto)
                    putExtra("SERVICE_NAME", notification.serviceName)
                }
                startActivity(intent)
            }
            "ad" -> {
                // Si es publicidad, también abrir calificación (según tu diseño)
                val intent = Intent(this, RatingActivity::class.java).apply {
                    putExtra("SERVICE_ID", notification.serviceId)
                    putExtra("PROVIDER_ID", notification.providerId)
                    putExtra("PROVIDER_NAME", notification.providerName)
                    putExtra("PROVIDER_PHOTO", notification.providerPhoto)
                    putExtra("SERVICE_NAME", notification.serviceName)
                }
                startActivity(intent)
            }
            else -> {
                // Otras notificaciones
            }
        }
    }

    private fun markAsRead(notificationId: String) {
        // TODO: Actualizar en Firestore
        val index = notificationsList.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val notification = notificationsList[index]
            notificationsList[index] = notification.copy(isRead = true)
            notificationsAdapter.notifyItemChanged(index)
        }
    }
}