package com.example.omg.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.omg.model.Postulante

interface ApiService {
    @POST("/registro")
    fun enviarDatos(@Body postulante: Postulante): Call<ResponseBody>
}
