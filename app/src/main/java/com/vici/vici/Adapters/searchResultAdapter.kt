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
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.activity_searchpage.*
import kotlinx.android.synthetic.main.search_result_recyclerviewcell.view.*

class searchResultAdapter(val mContext: Context, val data: ArrayList<AdModel>, val dataGrouped: List<Pair<String, java.util.ArrayList<AdModel>>>): RecyclerView.Adapter<SearchResultCell>() {

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
            holder.searchResultSubtitleTextView.text = StringConstants.SEE_ALL_AVAILABLE_PRODUCTS
        } else {
            val pos = position - dataGrouped.size
            title = data[pos].name.toString()
            val brand = data[pos].brand
            holder.searchResultTextView.text = title
            holder.searchResultSubtitleTextView.text = brand
        }

        holder.searchResult_cell_parent_view.setOnClickListener {

//            (mContext as Activity).startActivityForResult(intent, IntegerConstants.REQUEST_CODE_SUCCESS)
            val clickedItemIndex = holder.adapterPosition
            if (clickedItemIndex < dataGrouped.size) {
                val recentSearchGroupedSPList = getRecentSearchesGroupedFromSharedPrefernces()
                shouldAddGroup(recentSearchGroupedSPList, dataGrouped[clickedItemIndex])
                val intent = Intent(mContext, BrowseActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, title)
                val hashMapOfGroupedItem = serializeData(dataGrouped[clickedItemIndex])
                val bundle = Bundle()
                bundle.putSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, hashMapOfGroupedItem)
                intent.putExtras(bundle)
                (mContext as Activity).startActivity(intent)
            } else {
                val recentSearchesFromSharedPref = getRecentSearchesFromSharedPrefernces()
                shouldAdd(recentSearchesFromSharedPref, data[clickedItemIndex - dataGrouped.size])
                val intent = Intent(mContext, VAPActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE, data[clickedItemIndex - dataGrouped.size])
                intent.putExtras(bundle)
                (mContext as Activity).startActivity(intent)
            }

//            val intent = Intent(mContext, BrowsePageTest::class.java)
        }
    }

    private fun shouldAddGroup(recentSearchGroupedSPList: ArrayList<Pair<String, java.util.ArrayList<AdModel>>>, pair: Pair<String, java.util.ArrayList<AdModel>>) {
        for (groupedItem in recentSearchGroupedSPList) {
            if (groupedItem.first == pair.first) return
        }
        recentSearchGroupedSPList.add(0, pair)
        if (recentSearchGroupedSPList.size > 5) recentSearchGroupedSPList.removeAt(recentSearchGroupedSPList.size - 1)
        (mContext as Activity).recent_search_grouped_recyclerview.adapter = RecentSearchGroupAdapter(mContext, recentSearchGroupedSPList)

        storeRecentSearchGroupsToSharedPreferences(recentSearchGroupedSPList)
    }

    private fun serializeData(pair: Pair<String, ArrayList<AdModel>>): HashMap<String, ArrayList<AdModel>> {
        var hashMap = HashMap<String, ArrayList<AdModel>>()
        hashMap.put(pair.first, pair.second)
        return hashMap
    }


    private fun shouldAdd(recentSearchedList: ArrayList<AdModel>, latestRecent: AdModel) {
        for(recentSearch in recentSearchedList) {
            if (recentSearch.name.equals(latestRecent.name)) return
        }
        recentSearchedList.add(0, latestRecent)
        if (recentSearchedList.size > 5) recentSearchedList.removeAt(recentSearchedList.size - 1)
        (mContext as Activity).recent_search_recyclerview.adapter = RecentSearchAdapter(mContext, recentSearchedList)
        //store recent searches to sharedPreferences
        storeRecentSearchesToSharedPreferences(recentSearchedList)
    }

    private fun storeRecentSearchesToSharedPreferences(recentSearchedList: ArrayList<AdModel>) {
        val spEditor = sharedPref.edit()

        val gson = Gson()
        val jsonListString = gson.toJson(recentSearchedList)
        spEditor.putString(StringConstants.RECENT_SEARCH_LIST, jsonListString)
        spEditor.apply()
    }

    private fun storeRecentSearchGroupsToSharedPreferences(recentSearchedList: ArrayList<Pair<String, ArrayList<AdModel>>>) {
        val spEditor = sharedPref.edit()

        val gson = Gson()
        val jsonListString = gson.toJson(recentSearchedList)
        spEditor.putString(StringConstants.RECENT_SEARCH_LIST_GROUP, jsonListString)
        spEditor.apply()
    }

    private fun getRecentSearchesFromSharedPrefernces(): ArrayList<AdModel> {
        val gson = Gson()
        val json = sharedPref.getString(StringConstants.RECENT_SEARCH_LIST, "[]")
        val type = object : TypeToken<ArrayList<AdModel?>?>() {}.type
        return gson.fromJson(json, type)
    }

    private fun getRecentSearchesGroupedFromSharedPrefernces(): ArrayList<Pair<String, ArrayList<AdModel>>> {
        val gson = Gson()
        val json = sharedPref.getString(StringConstants.RECENT_SEARCH_LIST_GROUP, "[]")
        val type = object : TypeToken<ArrayList<Pair<String, ArrayList<AdModel>>?>?>() {}.type
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
