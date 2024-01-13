package com.example.riffrift.Retrofit

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface TrackAPI {
    @Headers(
        "X-RapidAPI-Key: 69afb91d30mshe48a54778fd59dep17eb65jsnb5c28d80838b",
        "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com"
    )
    @GET("search")
    suspend fun getData(@Query("q") query: String): TrackData
}

