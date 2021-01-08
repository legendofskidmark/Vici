package com.vici.vici.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vici.vici.R
import kotlinx.android.synthetic.main.ad_imageview.view.*

class ImageRecyclerViewAdapter(val mContext: Context, val imgUrls: ArrayList<String>) : RecyclerView.Adapter<AdsImageRecyclerView>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsImageRecyclerView {
        val cell = LayoutInflater.from(parent.context).inflate(R.layout.ad_imageview, parent, false)
        return AdsImageRecyclerView(cell)
    }

    override fun getItemCount() = imgUrls.size

    override fun onBindViewHolder(holder: AdsImageRecyclerView, position: Int) {
        Glide.with(mContext).load(imgUrls[position]).into(holder.imageView)
    }
}

class AdsImageRecyclerView(view: View): RecyclerView.ViewHolder(view) {
    val imageView = view.ad_imageview
}