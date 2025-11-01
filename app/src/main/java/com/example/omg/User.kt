package com.example.omg

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = "",
    val provider: String = "", // "google" o "phone"
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val favoriteServices: List<String> = emptyList() // 🆕 Los 4 servicios favoritos
) {
    // Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", "", "", 0, 0, emptyList())
}