package com.eka.googlemap

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {


    var marker: Marker? = null
    var latLng: LatLng? = null
    private lateinit var mMap: GoogleMap
    lateinit var gpsInfo: GpsInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            val close = resources.getDrawable(R.drawable.ic_close_24dp)
            setHomeAsUpIndicator(close)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        gpsInfo = GpsInfo(this)
        (supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.setOnMapClickListener {
            makeMarker(it)
        }
        checkPermission()
    }

    fun makeMarker(latLng: LatLng) {
        marker?.remove()
        marker = null

        MapHelper.latLngToAddress(latLng) {
            this.latLng = latLng
            marker = mMap.addMarker(MarkerOptions().apply {
                position(latLng)
                icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(it)))

            })
        }
    }

    fun getMarkerBitmap(address: String): Bitmap {
        val customMarkerView = (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.item_map_content, null)
        val addressView = customMarkerView.findViewById<TextView>(R.id.addressTextView)
        addressView.text = address

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0, customMarkerView.measuredWidth, customMarkerView.measuredHeight)

        customMarkerView.buildDrawingCache()

        val returnedBitmap = Bitmap.createBitmap(customMarkerView.measuredWidth, customMarkerView.measuredHeight,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        if (drawable != null)
            drawable.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()

        } else {
            setMyLocationEnabled()
        }
    }

    private fun requestPermission() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    1000)
        } else {
            setMyLocationEnabled()
        }
    }

    private fun setLocation() {
        gpsInfo.getLocation()
        if (gpsInfo.isGetLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(gpsInfo.latitude, gpsInfo.longitude), 17.0f))
        } else {
            Toast.makeText(this, "자신의 위치 불러오기 실패", Toast.LENGTH_SHORT).show()
        }
        Log.e("lat", "${gpsInfo.latitude}")
        Log.e("lon", "${gpsInfo.longitude}")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1000 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocationEnabled()
                } else {
                    Toast.makeText(this, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationEnabled() {
//        mMap.isMyLocationEnabled = true
        setLocation()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.ok -> {
                setResult(Activity.RESULT_OK, Intent().putExtra("latlng", latLng)) // parcelable
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }
}
