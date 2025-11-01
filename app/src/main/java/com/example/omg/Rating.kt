package com.example.omg

data class Rating(
    val id: String = "",
    val userId: String = "",
    val providerId: String = "",
    val serviceId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", 0, "", 0)
}