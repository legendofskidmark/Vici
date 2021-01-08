package com.vici.vici.Adapters

import android.content.Context
import android.content.Intent
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vici.vici.Activities.BrowseActivity
import com.vici.vici.Activities.BrowsePageTest
import com.vici.vici.Constants.IntegerConstants
import com.vici.vici.Constants.StringConstants
import com.vici.vici.models.Product
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import kotlinx.android.synthetic.main.activity_searchpage.*
import kotlinx.android.synthetic.main.search_result_recyclerviewcell.view.*

class searchResultAdapter(val mContext: Context, val data: List<QueryDocumentSnapshot>): RecyclerView.Adapter<SearchResultCell>() {

    val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(mContext, StringConstants.SHARED_PREF_FILE_NAME)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultCell {
        val cellForRow = LayoutInflater.from(parent.context).inflate(R.layout.search_result_recyclerviewcell, parent, false)
        return SearchResultCell(cellForRow)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: SearchResultCell, position: Int) {
        val title = data[position].get(StringConstants.NAME).toString()
        holder.searchResultTextView.text = title

        holder.searchResult_cell_parent_view.setOnClickListener {
            val intent = Intent(mContext, BrowseActivity::class.java)
            val recentSearchesFromSharedPref = getRecentSearchesFromSharedPrefernces()
            shouldAdd(recentSearchesFromSharedPref, title)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra(StringConstants.CLICKED_SEARCH_RESULT, title)
            (mContext as Activity).startActivityForResult(intent, IntegerConstants.REQUEST_CODE_SUCCESS)

//            val intent = Intent(mContext, BrowsePageTest::class.java)
//            (mContext as Activity).startActivity(intent)
        }
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
    val searchResult_cell_left_icon = view.cell_left_icon
    val searchResult_cell_right_icon = view.cell_right_icon
    val searchResult_cell_parent_view = view.cell_parent_view
}
