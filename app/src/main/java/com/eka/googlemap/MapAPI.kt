package com.eka.googlemap

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapAPI {
    @GET("/v1/map/reversegeocode")
    fun changeFromLatLng(@Query("query") latlng: String,
                         @Header("X-Naver-Client-Id") id: String,
                         @Header("X-Naver-Client-Secret") secret: String): Call<ResponseBody>
}