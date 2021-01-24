package com.vici.vici.Adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vici.vici.Activities.BrowseActivity
import com.vici.vici.Activities.NewUserActiviy
import com.vici.vici.Activities.VAPActivity
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.models.AdModel

class RecentSearchAdapter(val mContext: Context, val data: ArrayList<AdModel>): RecyclerView.Adapter<SearchResultCell>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCell {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.search_result_recyclerviewcell, parent, false)
        return SearchResultCell(cellForRow)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchResultCell, position: Int) {
        holder.searchResultTextView.text = data[position].name
        holder.searchResultSubtitleTextView.text = data[position].brand
        holder.searchResult_cell_left_icon.visibility = View.VISIBLE
        holder.searchResult_cell_parent_view.setOnClickListener {
            val intent = Intent(mContext, VAPActivity::class.java)
            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, data[position])
            mContext.startActivity(intent)
        }
    }
}

class RecentSearchGroupAdapter(val mContext: Context, val data: ArrayList<Pair<String, ArrayList<AdModel>>>): RecyclerView.Adapter<SearchResultCell>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCell {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.search_result_recyclerviewcell, parent, false)
        return SearchResultCell(cellForRow)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchResultCell, position: Int) {
        holder.searchResultTextView.text = data[position].first
        holder.searchResultSubtitleTextView.text = StringConstants.SEE_ALL_AVAILABLE_PRODUCTS
        holder.searchResult_cell_left_icon.visibility = View.VISIBLE
        holder.searchResult_cell_parent_view.setOnClickListener {
            val intent = Intent(mContext, BrowseActivity::class.java)
            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, data[position].first)
            val hashMapOfGroupedItem = serializeData(data[position])
            val bundle = Bundle()
            bundle.putSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, hashMapOfGroupedItem)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
        }
    }

    private fun serializeData(pair: Pair<String, ArrayList<AdModel>>): HashMap<String, ArrayList<AdModel>> {
        var hashMap = HashMap<String, ArrayList<AdModel>>()
        hashMap.put(pair.first, pair.second)
        return hashMap
    }
}