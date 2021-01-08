package com.vici.vici.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vici.vici.Activities.BrowseActivity
import com.vici.vici.Activities.NewUserActiviy
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R

class RecentSearchAdapter(val mContext: Context, val data: ArrayList<String>): RecyclerView.Adapter<SearchResultCell>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCell {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.search_result_recyclerviewcell, parent, false)
        return SearchResultCell(cellForRow)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchResultCell, position: Int) {
        holder.searchResultTextView.text = data[position]
        holder.searchResult_cell_left_icon.visibility = View.VISIBLE
        holder.searchResult_cell_parent_view.setOnClickListener {
            val intent = Intent(mContext, BrowseActivity::class.java)
            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, data[position])
            mContext.startActivity(intent)
        }
    }
}