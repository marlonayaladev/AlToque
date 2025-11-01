package com.example.omg

data class Provider(
    val id: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val service: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val priceRange: String = "",
    val averagePrice: String = "",
    val arrivalTime: String = "",
    val description: String = "",
    val experience: String = "",
    val servicesOffered: List<String> = emptyList(),
    val portfolio: List<String> = emptyList(), // URLs de fotos del portafolio
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val phone: String = "",
    val isAvailable: Boolean = true
) {
    constructor() : this("", "", "", "", 0f, 0, "", "", "", "", "", emptyList(), emptyList(), 0.0, 0.0, "", true)
}