package com.vici.vici.Adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.vici.vici.Activities.VAPActivity
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.browse_page_ad.view.*

class AdsResultAdapter(val mContext: Context, val response: ArrayList<AdModel>): RecyclerView.Adapter<AdsCell>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsCell {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.browse_page_ad, parent, false)
        return AdsCell(cell)
    }

    override fun onBindViewHolder(holder: AdsCell, position: Int) {
        val currentItem = response[position]
        holder.title.text = currentItem.name
        holder.address.text = currentItem.address
        holder.price.text = currentItem.price.toString()
        if (currentItem.distance == Double.MAX_VALUE) {
            holder.distance.text = "NA"
        } else {
            holder.distance.text = currentItem.distance.toString()
        }
        holder.rating.text = currentItem.rating

        holder.imgRecyclerView.adapter =
            currentItem.imgUrls?.let { ImageRecyclerViewAdapter(mContext, it) }
        holder.imgRecyclerView.layoutManager = LinearLayoutManager(mContext ,LinearLayoutManager.HORIZONTAL, false)

        holder.adCell.setOnClickListener {
            val intent = Intent(mContext, VAPActivity::class.java)

            val adDataBundle = Bundle()
            adDataBundle.putStringArrayList(StringConstants.VAP_AD_IMAGE_URLS, currentItem.imgUrls)
            adDataBundle.putString(StringConstants.VAP_AD_TITLE, currentItem.name)
            adDataBundle.putString(StringConstants.VAP_AD_ADDRESS, currentItem.address)
            adDataBundle.putString(StringConstants.VAP_AD_PRICE, currentItem.price.toString())
            adDataBundle.putString(StringConstants.VAP_AD_DISTANCE, currentItem.distance.toString())
            adDataBundle.putString(StringConstants.VAP_AD_RATING, currentItem.rating)

            intent.putExtras(adDataBundle)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount() = response.size
}

class AdsCell(cellView: View): RecyclerView.ViewHolder(cellView) {
    val title = cellView.ad_title_textview
    val address = cellView.address_textview
    val price = cellView.price_textview
    val distance = cellView.distance_textview
    val rating = cellView.rating_textview
    val adCell = cellView.ad_cell

    val imgRecyclerView = cellView.ad_image_recyclerview
}