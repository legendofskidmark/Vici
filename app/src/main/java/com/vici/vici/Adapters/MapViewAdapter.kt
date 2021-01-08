package com.vici.vici.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.vici.vici.R
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.mapview_cell.view.*

class MapViewAdapter(val mContext: Context, val response: ArrayList<AdModel>): RecyclerView.Adapter<MapViewCell>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewCell {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.mapview_cell, parent, false)
        return MapViewCell(cell)
    }

    override fun getItemCount() = response.size

    override fun onBindViewHolder(holder: MapViewCell, position: Int) {
        val currentAd = response[position]
        holder.mapViewtitle.text = currentAd.title
        holder.mapViewDistance.text = currentAd.distance.toString()
        holder.mapViewReview.text = "5*"

        holder.mapViewDirectioButton.setOnClickListener {
            Log.d("Boon", currentAd.latLng.toString())
            val gmmIntentUri = Uri.parse("google.navigation:q=17.185,78.4292")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            mapIntent.resolveActivity(mContext.packageManager)?.let {
                mContext.startActivity(mapIntent)
            }
        }
    }
}


class MapViewCell(cellView: View): RecyclerView.ViewHolder(cellView) {
    val mapViewtitle = cellView.ad_mapview_title_tv
    val mapViewReview = cellView.ad_mapview_review
    val mapViewDistance = cellView.ad_mapview_distance
    val mapViewDirectioButton = cellView.ad_mapview_directionbutton
    val mapViewCallButton = cellView.ad_mapview_callbutton
}