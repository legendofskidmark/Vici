package com.vici.vici.Activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.BounceInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.vici.vici.Adapters.MapViewAdapter
import com.vici.vici.R
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.activity_maps_view.*


class MapsViewActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var dummyResponse = ArrayList<AdModel>()
    var markerLatLongHM = HashMap<LatLng, Marker>()

    companion object {
        fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
            return ContextCompat.getDrawable(context, vectorResId)?.run {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val bitmap = Bitmap.createBitmap(intrinsicWidth + 30, intrinsicHeight + 30, Bitmap.Config.ARGB_8888)
                draw(Canvas(bitmap))
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_view)

        initMap()

        val url = ArrayList<String>()
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")

        val a = AdModel(url, "0", "Satyanarayan colony", 321.7, 567, "Rating |2.7", LatLng(17.25186, 78.41835))
        val b = AdModel(
            url,
            "1",
            "Satyanarayan colony",
            177.7,
            4123,
            "Rating |4.9",
            LatLng(17.4185, 78.4222)
        )
        val c = AdModel(
            url,
            "2",
            "Satyanarayan colony",
            707.7,
            98,
            "Rating |3.7",
            LatLng(17.4326, 78.4071)
        )
        val d = AdModel(
            url,
            "3",
            "Satyanarayan colony",
            7.78,
            5678,
            "Rating |41.7",
            LatLng(17.0289, 78.4605)
        )
        val e = AdModel(
            url,
            "4",
            "Satyanarayan colony",
            124.7,
            462,
            "Rating |9.7",
            LatLng(17.986, 78.415)
        )
        val f = AdModel(
            url,
            "5",
            "Satyanarayan colony",
            97.7,
            59,
            "Rating |45.7",
            LatLng(17.2596, 78.545)
        )
        val g = AdModel(
            url,
            "6",
            "Satyanarayan colony",
            17.7,
            43,
            "Rating |687.7",
            LatLng(17.186, 78.4135)
        )
        val h = AdModel(
            url,
            "7",
            "Satyanarayan colony",
            789.7,
            867,
            "Rating |5.7",
            LatLng(17.286, 78.1835)
        )


        dummyResponse.add(a)
        dummyResponse.add(b)
        dummyResponse.add(c)
        dummyResponse.add(d)
        dummyResponse.add(e)
        dummyResponse.add(f)
        dummyResponse.add(g)
        dummyResponse.add(h)

        map_view_recyclerview.adapter = MapViewAdapter(this, dummyResponse)
        map_view_recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(map_view_recyclerview)

    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.ads_map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        addMarkersOnMap()

        map_view_recyclerview.addOnScrollListener(RecyclerViewScrollListener(this, mMap, dummyResponse, markerLatLongHM, markerLatLongHM.toList()))
    }

    private fun addMarkersOnMap() {
        var flag = true
        for (ad in dummyResponse) {
            if (flag) {
                markerLatLongHM[ad.latLng] = mMap.addMarker(MarkerOptions().position(ad.latLng).title("Marker in "))
                flag = false
                continue
            }
            markerLatLongHM[ad.latLng] = mMap.addMarker(MarkerOptions().position(ad.latLng).title("Marker in ${ad.latLng.toString()}").icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_marker)))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dummyResponse[0].latLng, 10f))
    }
}

class RecyclerViewScrollListener(val mContext: Context, val mapRef: GoogleMap, val mapDataPoints: ArrayList<AdModel>, val markerLatLngHM: HashMap<LatLng, Marker>, val marketLatLngList: List<Pair<LatLng, Marker>>) : RecyclerView.OnScrollListener() {

    var indexOfCurrentLatLong: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        Toast.makeText(mContext, ((recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()).toString(), Toast.LENGTH_LONG).show()
        val index = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (mapRef != null && index != -1) {
            indexOfCurrentLatLong = index
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            mapRef.moveCamera(CameraUpdateFactory.newLatLngZoom(mapDataPoints[indexOfCurrentLatLong].latLng, 10f))
            mapRef.moveCamera(CameraUpdateFactory.scrollBy(10f, 10f))
            bounceMarker(mapDataPoints[indexOfCurrentLatLong].latLng)
        }
    }

    private fun bounceMarker(latLng: LatLng) {
        for(point in marketLatLngList) {
            markerLatLngHM[point.first]?.remove()
            if (point.first == latLng) {
                markerLatLngHM[point.first] = mapRef.addMarker(MarkerOptions().position(latLng).title("Marker in "))
                continue
            }
            markerLatLngHM[point.first] = mapRef.addMarker(MarkerOptions().position(point.first).title("Marker in ${point.toString()}").icon(MapsViewActivity.bitmapDescriptorFromVector(mContext, R.drawable.ic_map_marker)))
        }
    }
}