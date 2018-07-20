package com.eka.googlemap

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MapHelper {
    fun latLngToAddress(latLng: LatLng, onAddressChanged: (String) -> Unit) {
        var returnAddress = ""
        NetworkHelper.mapInstance.changeFromLatLng("${latLng.longitude}, ${latLng.latitude}",
                "WAtJVVGhyrZDGy4TGVXW",
                "AtG79bTvJh").enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                try {
                    val json = JSONObject(response?.body()?.string())
                            .getJSONObject("result")

                            .getJSONArray("items").getJSONObject(0).getJSONObject("addrdetail")
                    returnAddress = "${formatSido(json.getString("sido"))} ${json.getString("sigugun")} " +
                            "${json.getString("dongmyun")} ${json.getString("rest")}"
                    onAddressChanged.invoke(returnAddress)
                } catch (e: Exception) {
                    Log.e("asdf", e.message)
                    return
                }
            }
        })
    }

    fun formatSido(sido: String) = when (sido) {
        "서울특별시" ->
            "서울시"
        else ->
            sido
    }
}