package com.vici.vici.Adapters

import android.content.Context
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vici.vici.Activities.BrowseActivity
import com.vici.vici.Activities.VAPActivity
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import kotlinx.android.synthetic.main.activity_searchpage.*
import kotlinx.android.synthetic.main.search_result_recyclerviewcell.view.*

class searchResultAdapter(val mContext: Context, val data: List<HashMap<String, String>>, val dataGrouped: List<Pair<String, ArrayList<HashMap<String, String>>>>): RecyclerView.Adapter<SearchResultCell>() {

    val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(mContext, StringConstants.SHARED_PREF_FILE_NAME)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCell {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.search_result_recyclerviewcell, parent, false)
        return SearchResultCell(cellForRow)
    }

    override fun getItemCount() = data.size + dataGrouped.size

    override fun onBindViewHolder(holder: SearchResultCell, position: Int) {
        var title = ""
        if (position < dataGrouped.size) {
            title = dataGrouped[position].first.toLowerCase().capitalize()
            holder.searchResultTextView.text = title
            holder.searchResultSubtitleTextView.text = "See all available products"
        } else {
            val pos = position - dataGrouped.size
            title = data[pos].get(StringConstants.NAME).toString()
            val brand = data[pos].get(StringConstants.BRAND).toString()
            holder.searchResultTextView.text = title
            holder.searchResultSubtitleTextView.text = brand
        }

        holder.searchResult_cell_parent_view.setOnClickListener {

//            val recentSearchesFromSharedPref = getRecentSearchesFromSharedPrefernces()
//            shouldAdd(recentSearchesFromSharedPref, title)
//            (mContext as Activity).startActivityForResult(intent, IntegerConstants.REQUEST_CODE_SUCCESS)
            val clickedItemIndex = holder.adapterPosition
            if (clickedItemIndex < dataGrouped.size) {
                val intent = Intent(mContext, BrowseActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, title)
                val hashMapOfGroupedItem = serializeData(dataGrouped[clickedItemIndex])
                val bundle = Bundle()
                bundle.putSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, hashMapOfGroupedItem)
                intent.putExtras(bundle)
                (mContext as Activity).startActivity(intent)
            } else {
                val intent = Intent(mContext, VAPActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, data[clickedItemIndex - dataGrouped.size])
                intent.putExtras(bundle)
                (mContext as Activity).startActivity(intent)
            }

//            val intent = Intent(mContext, BrowsePageTest::class.java)
        }
    }

    private fun serializeData(pair: Pair<String, ArrayList<HashMap<String, String>>>): HashMap<String, ArrayList<HashMap<String, String>>> {
        var hashMap = HashMap<String, ArrayList<HashMap<String, String>>>()
        hashMap.put(pair.first, pair.second)
        return hashMap
    }


    private fun shouldAdd(recentSearchedList: ArrayList<String>, latestRecent: String) {
        for(recentSearch in recentSearchedList) {
            if (recentSearch.equals(latestRecent, true)) return
        }
        recentSearchedList.add(0, latestRecent)
        if (recentSearchedList.size > 10) recentSearchedList.removeAt(recentSearchedList.size - 1)
        (mContext as Activity).recent_search_recyclerview.adapter = RecentSearchAdapter(mContext, recentSearchedList)
        //store recent searches to sharedPreferences
        storeRecentSearchesToSharedPreferences(recentSearchedList)
    }

    private fun storeRecentSearchesToSharedPreferences(recentSearchedList: ArrayList<String>) {
        val spEditor = sharedPref.edit()

        val gson = Gson()
        val jsonListString = gson.toJson(recentSearchedList)
        spEditor.putString(StringConstants.RECENT_SEARCH_LIST, jsonListString)
        spEditor.apply()
    }

    private fun getRecentSearchesFromSharedPrefernces(): ArrayList<String> {
        val gson = Gson()
        val json = sharedPref.getString(StringConstants.RECENT_SEARCH_LIST, "[]")
        val type = object : TypeToken<java.util.ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type)
    }
}

class SearchResultCell(view: View): RecyclerView.ViewHolder(view) {
    val searchResultTextView = view.search_result_cell_textview
    val searchResultSubtitleTextView = view.search_result_cell_subTitle_textview
    val searchResult_cell_left_icon = view.cell_left_icon
    val searchResult_cell_right_icon = view.cell_right_icon
    val searchResult_cell_parent_view = view.cell_parent_view
}
