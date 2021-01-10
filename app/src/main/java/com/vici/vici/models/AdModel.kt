package com.vici.vici.models

import com.google.android.gms.maps.model.LatLng

data class AdModel(val imgUrls: ArrayList<String>, val title: String, val address: String, val price: Double, val distance: Double, val rating: String, val latLng: LatLng)