package com.example.omg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView  // 🆕 CAMBIADO
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProvidersAdapter(
    private val providers: List<Provider>,
    private val onProviderClick: (Provider) -> Unit
) : RecyclerView.Adapter<ProvidersAdapter.ProviderViewHolder>() {

    inner class ProviderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProviderPhoto: ImageView = view.findViewById(R.id.ivProviderPhoto)  // 🆕 CAMBIADO
        val tvProviderName: TextView = view.findViewById(R.id.tvProviderName)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvReviews: TextView = view.findViewById(R.id.tvReviews)
        val tvArrivalTime: TextView = view.findViewById(R.id.tvArrivalTime)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvBadge: TextView = view.findViewById(R.id.tvBadge)

        fun bind(provider: Provider) {
            tvProviderName.text = provider.name
            tvRating.text = provider.rating.toString()
            tvReviews.text = "(${provider.reviewCount})"
            tvArrivalTime.text = "⏱ ${provider.arrivalTime}"
            tvPrice.text = "💰 ${provider.priceRange}"

            // Badge
            tvBadge.text = if (provider.isAvailable) "DISPONIBLE" else "OCUPADO"
            tvBadge.setBackgroundResource(
                if (provider.isAvailable) R.drawable.bg_badge_orange
                else R.drawable.bg_button_gray
            )

            // Cargar foto con Glide
            if (provider.photoUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(provider.photoUrl)
                    .placeholder(R.drawable.ic_user)
                    .into(ivProviderPhoto)
            }

            // Click
            itemView.setOnClickListener {
                onProviderClick(provider)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_provider, parent, false)
        return ProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) {
        holder.bind(providers[position])
    }

    override fun getItemCount() = providers.size
}