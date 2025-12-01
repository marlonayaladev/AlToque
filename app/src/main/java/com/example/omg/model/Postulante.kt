package com.example.omg.model

data class Postulante(
    val nombre: String,
    val dni: String,
    val celular: String, // ✅ Nuevo campo agregado
    val fecha_nacimiento: String,
    val correo: String,
    val servicio: String,
    val licencia_municipal_base64: String?,
    val cul_base64: String?
)
