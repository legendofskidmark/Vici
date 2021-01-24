package com.vici.vici.models

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class AdModel: Serializable {
//    val title: String, val address: String, val price: Double, val distance: Double, val rating: String, val latLng: String
    var emailID : String? = null
    var imgUrls: ArrayList<String>? = null
    var brand: String? = null
    var name: String? = null
    var modelType: String? = null
    var age: String? = null
    var perTime: String? = null
    var price: Double? = null
    var latLong: String? = null
    var distance: Double? = null
    var address: String? = null
    var rating: String? = null
    var isGrouped: Boolean = false
    var ads: ArrayList<AdModel>? = null
}