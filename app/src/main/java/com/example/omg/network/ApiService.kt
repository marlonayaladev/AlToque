package com.example.omg.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.example.omg.model.Postulante
import com.example.omg.model.EstadoResponse

interface ApiService {
    @POST("registro")
    fun enviarDatos(@Body postulante: Postulante): Call<ResponseBody>

    @GET("verificar_estado")
    fun verificarEstado(@Query("correo") correo: String): Call<EstadoResponse>
}
