package com.ysifre.projet

import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class API {
}

data class Response(
    @SerializedName("routes")
    val routes: Array<Routes>,
)

data class Routes(
    @SerializedName("legs")
    val legs: Array<Legs>
)

data class Legs(
    @SerializedName("duration")
    val duration: DurationObject
)

data class DurationObject(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int,
    @SerializedName("end_address")
    val end_address: String
)

interface GoogleMapsAPI {
    @GET("/maps/api/directions/json")
    fun getDurationObject(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String = "AIzaSyABivQ89A5qBus4wv_ZdOk0R25eQVj_xFU"
    ): Deferred<Response>
}

object NetworkManagerDuration {
    private val api = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
        .create(GoogleMapsAPI::class.java)

    suspend fun getDuration(origin: String, destination: String): Deferred<Response> {
        return api.getDurationObject(origin, destination)
    }
}
