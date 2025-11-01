package com.example.omg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class NotificationsAdapter(
    private val notifications: List<Notification>,
    private val onNotificationClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adHeader: LinearLayout = view.findViewById(R.id.adHeader)
        val tvAdText: TextView = view.findViewById(R.id.tvAdText)
        val mainContent: LinearLayout = view.findViewById(R.id.mainContent)
        val tvNotificationText: TextView = view.findViewById(R.id.tvNotificationText)
        val tvTimestamp: TextView = view.findViewById(R.id.tvTimestamp)

        fun bind(notification: Notification) {
            // Mostrar texto principal
            tvNotificationText.text = notification.message

            // Mostrar publicidad si es tipo "ad"
            if (notification.type == "ad" && notification.adText.isNotEmpty()) {
                adHeader.visibility = View.VISIBLE
                tvAdText.text = notification.adText
            } else {
                adHeader.visibility = View.GONE
            }

            // Formatear timestamp
            tvTimestamp.text = getTimeAgo(notification.timestamp)

            // Cambiar fondo según si está leída
            if (notification.isRead) {
                itemView.alpha = 0.6f
            } else {
                itemView.alpha = 1f
            }

            // Click
            itemView.setOnClickListener {
                onNotificationClick(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    private fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "Justo ahora"
            diff < 3600000 -> "Hace ${diff / 60000} minutos"
            diff < 86400000 -> "Hace ${diff / 3600000} horas"
            diff < 604800000 -> "Hace ${diff / 86400000} días"
            else -> {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }
}