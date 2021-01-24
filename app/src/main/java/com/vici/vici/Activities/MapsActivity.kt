package com.vici.vici.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vici.vici.Constants.StringConstants
import com.vici.vici.models.Product
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import com.vici.vici.Util.Utility
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.recent_items.*
import java.io.Serializable
import java.util.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    companion object {
        var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    }
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var searchBarSuggestionsList: MutableList<Product>

    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234
    private val DEFAULT_ZOOM = 15f
    private var mLocationPermissionsGranted = false
    var bb = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        homePage_SearchBar.setOnClickListener {
            startActivity(Intent(this, SearchpageActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

        homePageBottomSheetHandler()
        Utility.hideActionBar(supportActionBar, window)
//        handleSearchBarRequests()

    }

    override fun onStart() {
        super.onStart()

        getLocationPermission()
        val sp = SharedPreferencesUtility.openSharedPreferencesWith(this, StringConstants.SHARED_PREF_FILE_NAME)

        if(sp.contains(StringConstants.RECENT_SEARCH_LIST)) {
            if (no_recent_search_textview != null) no_recent_search_textview.visibility = View.GONE
            val gson = Gson()
            val json = sp.getString(StringConstants.RECENT_SEARCH_LIST, "[]")
            val type = object : TypeToken<ArrayList<AdModel?>?>() {}.type
            val recentList: ArrayList<AdModel> = gson.fromJson(json, type)
            val recentItemChipGroup = homepage_bottomsheet_View.findViewById<LinearLayout>(R.id.recent_items_view).findViewById<ChipGroup>(R.id.recent_chipGroup)
            recentItemChipGroup.removeAllViews()
            for(item in recentList) {
                val chipView = configureChip(item)
                recentItemChipGroup.addView(chipView)
            }
        }

        if(sp.contains(StringConstants.RECENT_SEARCH_LIST_GROUP)) {
            val gson = Gson()
            val json = sp.getString(StringConstants.RECENT_SEARCH_LIST_GROUP, "[]")
            val type = object : TypeToken<ArrayList<Pair<String, ArrayList<AdModel>>?>?>() {}.type
            val recentList: ArrayList<Pair<String, ArrayList<AdModel>>> = gson.fromJson(json, type)
            val recentItemChipGroup = homepage_bottomsheet_View.findViewById<LinearLayout>(R.id.recent_items_view).findViewById<ChipGroup>(R.id.recent_chipGroup)
            recentItemChipGroup.removeAllViews()
            for(item in recentList) {
                val chipView = configureChip(item)
                recentItemChipGroup.addView(chipView)
            }
        }

    }

    private fun configureChip(item: AdModel): Chip {
        val newChip = Chip(this)
        newChip.text = item.name
        newChip.setOnClickListener {
            val intent = Intent(this, VAPActivity::class.java)
//            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, item)
//            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, item.name)
            startActivity(intent)
        }
        return newChip
    }

    private fun configureChip(item: Pair<String, ArrayList<AdModel>>): Chip {
        val newChip = Chip(this)
        newChip.text = item.first
        newChip.setOnClickListener {
            val intent = Intent(this, BrowseActivity::class.java)

            val hashMapOfGroupedItem = serializeData(item)
            val bundle = Bundle()
            bundle.putSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, hashMapOfGroupedItem)
            intent.putExtras(bundle)
            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, item.first)
            startActivity(intent)
        }
        return newChip
    }

    private fun serializeData(pair: Pair<String, ArrayList<AdModel>>): HashMap<String, ArrayList<AdModel>> {
        var hashMap = HashMap<String, ArrayList<AdModel>>()
        hashMap.put(pair.first, pair.second)
        return hashMap
    }

    private fun handleSearchBarRequests() {

        searchBarSuggestionsList = mutableListOf(
            Product("apple"),
            Product("boon"),
            Product("ben"),
            Product("zet"),
            Product("zebra"),
            Product("aeroplane"),
            Product("giraffe"),
            Product("lion"),
            Product("boo boo"),
            Product("chub"),
            Product("poo poo"),
            Product("vampire"),
            Product("vampire"),
            Product("vampire"),
            Product("vampire"),
            Product("vampire")
        )
//
//        val adapter = HPSearchAdapter(
//            this,
//            R.layout.activity_maps,
//            R.id.hpSearchBar,
//            searchBarSuggestionsList
//        )
//        hpSearchBar.setAdapter(adapter)
    }

    private fun homePageBottomSheetHandler() {
        mBottomSheetBehavior = BottomSheetBehavior.from(homepage_bottomsheet_View)
        homepage_bottomsheet_View.setOnClickListener {
            if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
    }

    private fun getLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (ContextCompat.checkSelfPermission(this.applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.applicationContext, COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true
                initMap()
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        mLocationPermissionsGranted = false
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false
                            return
                        }
                        i++
                    }
                    mLocationPermissionsGranted = true
                    //initialize our map
                    initMap()
                }
            }
        }
    }

    private fun getDeviceLocation() {
        if (mFusedLocationProviderClient == null) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }
        try {
            if (mLocationPermissionsGranted) {
                val location = mFusedLocationProviderClient!!.lastLocation
                location.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentLocation: Location? = task.result as Location?
                        if (currentLocation != null) {
                            moveCamera(
                                LatLng(
                                    currentLocation.getLatitude(),
                                    currentLocation.getLongitude()
                                ),
                                DEFAULT_ZOOM
                            )
                        }
                    } else {
                        print("unable to get current location")
                    }
                }
            }
        } catch (e: SecurityException) {

        }
    }

    private fun moveCamera(latLng: LatLng, zoom: Float) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

//        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show()
        mMap = googleMap

        if (mLocationPermissionsGranted) {
            getDeviceLocation()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    // For keyboard dismissal
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}