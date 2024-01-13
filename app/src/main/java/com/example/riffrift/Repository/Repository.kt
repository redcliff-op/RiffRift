package com.example.riffrift.Repository

import com.example.riffrift.Retrofit.RetrofitInstance
import com.example.riffrift.Retrofit.TrackData

class Repository(val retrofitInstance: RetrofitInstance) {
    suspend fun getDataFromRetrofit(query: String): TrackData {
        return retrofitInstance.trackAPI.getData(query)
    }
}