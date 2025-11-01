package com.example.omg

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "", // "service_completed", "rating_reminder", "general", "ad"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val serviceId: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val providerPhoto: String = "",
    val serviceName: String = "",
    val adText: String = "" // Para notificaciones de publicidad
) {
    constructor() : this("", "", "", "", 0, false, "", "", "", "", "", "")
}