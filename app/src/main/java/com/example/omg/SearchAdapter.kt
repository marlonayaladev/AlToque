package com.example.omg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(
    private val services: List<String>,
    private val onServiceClick: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServiceName: TextView = view.findViewById(R.id.tvServiceName)
        val tvServiceIcon: TextView = view.findViewById(R.id.tvServiceIcon)

        fun bind(service: String) {
            tvServiceName.text = service

            // Asignar icono según el servicio
            tvServiceIcon.text = getServiceIcon(service)

            itemView.setOnClickListener {
                onServiceClick(service)
            }
        }

        private fun getServiceIcon(service: String): String {
            return when (service) {
                "Gasfitería" -> "🔧"
                "Jardinería" -> "🌿"
                "Repartidor de Gas" -> "🔥"
                "Servicio de Limpieza" -> "🧹"
                "Mozos" -> "🍽️"
                "Masajista" -> "💆"
                "Manicure" -> "💅"
                "Técnico" -> "⚙️"
                "Profesores" -> "📚"
                "Electricista" -> "💡"
                "Plomería" -> "🚿"
                "Carpintería" -> "🪚"
                "Pintura" -> "🎨"
                "Delivery" -> "🚚"
                "Seguridad" -> "🛡️"
                else -> "🔍"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size
}