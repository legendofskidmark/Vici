package com.vici.vici.Activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vici.vici.Activities.MainActivity.Companion.db
import com.vici.vici.Adapters.RecentSearchAdapter
import com.vici.vici.Adapters.searchResultAdapter
import com.vici.vici.Constants.IntegerConstants
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import com.vici.vici.Util.Utility
import kotlinx.android.synthetic.main.activity_searchpage.*
import java.util.*


class SearchpageActivity: AppCompatActivity() {

    val mTAG = "SearchpageActivity"
    lateinit var searchBarSuggestionsList: MutableList<QueryDocumentSnapshot>
    var filteredList: MutableList<QueryDocumentSnapshot> = mutableListOf()
    var recentSearchedList: ArrayList<String> = ArrayList()
    var didHitApi = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchpage)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        Utility.hideActionBar(supportActionBar, window)
        configureSearchBar()

        Utility.showKeyboard(applicationContext)
        search_page_contentview.setOnClickListener { Utility.hideKeyboard(applicationContext, window) }

        searchpage_Searchbar.setStartIconOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        handleRecentSearches()
        didHitApi = false
    }

    private fun handleRecentSearches() {
        recent_search_recyclerview.layoutManager = LinearLayoutManager(this)
        val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(this, StringConstants.SHARED_PREF_FILE_NAME)

        if (sharedPref.contains(StringConstants.RECENT_SEARCH_LIST)) {
            val recentlySearchedList = getRecentSearchesFromSharedPref(sharedPref)
            recent_search_recyclerview.adapter = RecentSearchAdapter(this, recentlySearchedList)
        }
    }

    private fun getRecentSearchesFromSharedPref(sp: SharedPreferences): ArrayList<String> {
        val gson = Gson()
        val json = sp.getString(StringConstants.RECENT_SEARCH_LIST, "[]")
        var type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson(json, type)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
        Utility.hideKeyboard(this, window)
    }

    private fun configureSearchBar() {

        searchpage_Searchbar.editText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query == null || query.isEmpty()) {
                    saved_items_search_seperator.visibility = View.GONE
                    search_result_recyclerview.visibility = View.GONE
                } else {
                    handleSearchResults(query)
                    if (!filteredList.isEmpty()) {
                        saved_items_search_seperator.visibility = View.VISIBLE
                        search_result_recyclerview.visibility = View.VISIBLE
                    }
//                    filteredList.clear()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredList.clear()
            }
        })
    }

    private fun handleSearchResults(query: String) {

        if (!didHitApi) {
            searchBarSuggestionsList = mutableListOf()
            filteredList.clear()
            db.collection(StringConstants.ITEM_DETAILS).get()
                .addOnSuccessListener { documentReference ->
                    for (doc in documentReference.documents) {
                        doc.reference.collection(StringConstants.ITEMS).get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val itemsList = task.result.toList()
                                    for (item in itemsList) {
                                        searchBarSuggestionsList.add(item)
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Something went wrong. Please try again later",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.e(mTAG, "Search Filter Listings Failed")
                                    return@addOnCompleteListener
                                }
                            }
                    }

                    for(item in searchBarSuggestionsList) {
                        if (item[StringConstants.NAME].toString().contains(query)) {
                            filteredList.add(item)
                        }
                    }
                    search_result_recyclerview.layoutManager =
                        LinearLayoutManager(this)
                    search_result_recyclerview.adapter =
                        searchResultAdapter(this, filteredList)
                    didHitApi = true
                }
        } else {
            for(ad in searchBarSuggestionsList) {
                if (ad[StringConstants.NAME].toString().contains(query)) {
                    filteredList.add(ad)
                }
            }
            search_result_recyclerview.layoutManager = LinearLayoutManager(this)
            search_result_recyclerview.adapter = searchResultAdapter(this, filteredList)
            (search_result_recyclerview.adapter as searchResultAdapter).notifyDataSetChanged()
        }


//        TODO("Always take 9 top results the logic for which to be written will be in firebase")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntegerConstants.REQUEST_CODE_SUCCESS) {
            if (resultCode == Activity.RESULT_OK) {
                val queriedString = data?.getStringExtra(StringConstants.QUERIED_SEARCH_RESULT)
                if (queriedString != null) {
                    searchpage_Searchbar.editText?.setText(queriedString)
                    searchpage_Searchbar.editText?.setSelection(queriedString.length)
                }
            } else {
                searchpage_Searchbar.editText?.setText("")
            }
        } else {
            searchpage_Searchbar.editText?.setText("")
        }
    }
}

