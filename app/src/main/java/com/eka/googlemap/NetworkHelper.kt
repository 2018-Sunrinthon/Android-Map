package com.eka.googlemap

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkHelper {

    private const val mapUrl = "https://openapi.naver.com/"
    val mapInstance: MapAPI
        get() {
            if (mapRetrofit == null) {
                mapRetrofit = Retrofit.Builder()
                        .baseUrl(mapUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return mapRetrofit!!.create<MapAPI>(MapAPI::class.java)
        }
    var mapRetrofit: Retrofit? = null
}