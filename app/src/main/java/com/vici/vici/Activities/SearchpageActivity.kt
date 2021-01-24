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
import com.vici.vici.Adapters.RecentSearchGroupAdapter
import com.vici.vici.Adapters.searchResultAdapter
import com.vici.vici.Constants.IntegerConstants
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import com.vici.vici.Util.Utility
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.activity_searchpage.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SearchpageActivity: AppCompatActivity() {

    val mTAG = "SearchpageActivity"
    lateinit var searchBarSuggestionsList: MutableList<QueryDocumentSnapshot>
    var filteredList = ArrayList<AdModel>()
    var filteredListGroup = HashMap<String, ArrayList<AdModel>>()
    var groupedItems = HashMap<String, ArrayList<AdModel>>()
    var allItemsList = ArrayList<AdModel>()
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

    private fun setGroupedItems() {
        loader_view.visibility = View.VISIBLE
        db.collection(StringConstants.ORGANIZED_GROUP).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (brand in task.result.documents) {
                    brand.reference.collection(StringConstants.PRODUCTS).get().addOnCompleteListener { productTask ->
                        if (productTask.isSuccessful) {
                            for (doc in productTask.result.documents) {
                                val brand = doc[StringConstants.BRAND]
                                val itemOwnerMailID = doc[StringConstants.USER_EMAIL_ID].toString()

                                db.collection(StringConstants.UsersDBName).document(itemOwnerMailID).get().addOnCompleteListener { innerTask ->
                                    if (innerTask.isSuccessful) {
                                        val latLong = innerTask.result[StringConstants.LAT_LONG].toString()

                                        val details = hashMapOf(
                                            StringConstants.USER_EMAIL_ID to itemOwnerMailID,
                                            StringConstants.BRAND to brand.toString(),
                                            StringConstants.NAME to doc[StringConstants.NAME].toString(),
                                            StringConstants.MODEL to doc[StringConstants.MODEL].toString(),
                                            StringConstants.AGE to doc[StringConstants.AGE].toString(),
                                            StringConstants.PER_TIME to doc[StringConstants.PER_TIME].toString(),
                                            StringConstants.PRICE to doc[StringConstants.PRICE].toString(),
                                            StringConstants.LAT_LONG to latLong
                                        )

                                        val groupedAdModel = AdModel()
                                        groupedAdModel.emailID = itemOwnerMailID
                                        groupedAdModel.brand = brand.toString()
                                        groupedAdModel.name = doc[StringConstants.NAME].toString()
                                        groupedAdModel.modelType = doc[StringConstants.MODEL].toString()
                                        groupedAdModel.age = doc[StringConstants.AGE].toString()
                                        groupedAdModel.perTime = doc[StringConstants.PER_TIME].toString()
                                        groupedAdModel.price = doc[StringConstants.PRICE].toString().toDouble()
                                        groupedAdModel.latLong = latLong
                                        groupedAdModel.isGrouped = true

                                        allItemsList.add(groupedAdModel)

                                        if (groupedItems[brand.toString().toUpperCase()].isNullOrEmpty()) {
                                            val arrayList = ArrayList<AdModel>()
                                            arrayList.add(groupedAdModel)
                                            groupedItems[brand.toString().toUpperCase()] = arrayList
                                        } else {
                                            groupedItems[brand.toString().toUpperCase()]?.add(groupedAdModel)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (task.result.documents.last() == brand) {
                        loader_view.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        filteredList.clear()
//        filteredListGroup.clear()
        groupedItems.clear()
        setGroupedItems()
        handleRecentSearches()
        didHitApi = false
    }

    private fun handleRecentSearches() {
        recent_search_grouped_recyclerview.layoutManager = LinearLayoutManager(this)
        recent_search_recyclerview.layoutManager = LinearLayoutManager(this)
        val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(this, StringConstants.SHARED_PREF_FILE_NAME)

        if (sharedPref.contains(StringConstants.RECENT_SEARCH_LIST_GROUP)) {
            val recentlySearchedList = getRecentSearcheGroupFromSharedPref(sharedPref)
            recent_search_grouped_recyclerview.adapter = RecentSearchGroupAdapter(this, recentlySearchedList)
        }

        if (sharedPref.contains(StringConstants.RECENT_SEARCH_LIST)) {
            val recentlySearchedList = getRecentSearchesFromSharedPref(sharedPref)
            recent_search_recyclerview.adapter = RecentSearchAdapter(this, recentlySearchedList)
        }
    }

    private fun getRecentSearchesFromSharedPref(sp: SharedPreferences): ArrayList<AdModel> {
        val gson = Gson()
        val json = sp.getString(StringConstants.RECENT_SEARCH_LIST, "[]")
        var type = object : TypeToken<ArrayList<AdModel?>?>() {}.type
        return gson.fromJson(json, type)
    }

    private fun getRecentSearcheGroupFromSharedPref(sp: SharedPreferences): ArrayList<Pair<String, java.util.ArrayList<AdModel>>> {
        val gson = Gson()
        val json = sp.getString(StringConstants.RECENT_SEARCH_LIST_GROUP, "[]")
        var type = object : TypeToken<ArrayList<Pair<String, ArrayList<AdModel?>?>?>?>() {}.type
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
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredList.clear()
                filteredListGroup.clear()
            }
        })
    }

    private fun handleSearchResults(query: String) {

        for (brandedItem in groupedItems) {
            if (brandedItem.value.size > 1) {
                for (item in brandedItem.value) {
                    if (doesItemContainsQueryString(item, brandedItem.key, query)) {
                        filteredListGroup[brandedItem.key] = brandedItem.value
                        break
                    }
                }
            }
        }

        for (brandedItem in groupedItems) {
            for (item in brandedItem.value) {
                if (doesItemContainsQueryString(item, brandedItem.key, query)) {
                    filteredList.add(item)
                }
            }
        }

        search_result_recyclerview.layoutManager = LinearLayoutManager(this)
        search_result_recyclerview.adapter = searchResultAdapter(this, filteredList, filteredListGroup.toList())
        (search_result_recyclerview.adapter as searchResultAdapter).notifyDataSetChanged()

    }

    private fun doesItemContainsQueryString(item: AdModel, brandName: String, query: String): Boolean {
        if (item.name?.contains(query, ignoreCase = true) == true || item.modelType?.contains(query, ignoreCase = true) == true || brandName.contains(query, ignoreCase = true)) return true
        return false
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

