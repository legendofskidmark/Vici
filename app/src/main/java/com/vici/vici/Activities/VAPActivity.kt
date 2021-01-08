package com.vici.vici.Activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.vici.vici.Adapters.ImageRecyclerViewAdapter
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.Utility
import kotlinx.android.synthetic.main.activity_vap.*
import kotlinx.android.synthetic.main.vap_lend_layout.view.*


class VAPActivity: AppCompatActivity() {

    var imageURLs: ArrayList<String>? = null
    var title: String? = null
    var address: String? = null
    var price: String? = null
    var distance: String? = null
    var rating: String? = null
    lateinit var imageRecyclerView: RecyclerView
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vap)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        Utility.hideActionBar(supportActionBar, window)

        fillVAPAttributes()
        initView()
        configureBottomStickyButtons()

    }

    private fun configureBottomStickyButtons() {




    }

    private fun initView() {
        imageRecyclerView = vap_img_recyclerview

        configureImgRecyclerView()
    }

    private fun configureImgRecyclerView() {
        imageRecyclerView.adapter = imageURLs?.let { ImageRecyclerViewAdapter(this, it) }
        imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun fillVAPAttributes() {
        val bundleData = intent.extras

        if (bundleData != null) {
            imageURLs = bundleData.getStringArrayList(StringConstants.VAP_AD_IMAGE_URLS)
            title = bundleData.getString(StringConstants.VAP_AD_TITLE)
            address = bundleData.getString(StringConstants.VAP_AD_ADDRESS)
            price = bundleData.getString(StringConstants.VAP_AD_PRICE)
            distance = bundleData.getString(StringConstants.VAP_AD_DISTANCE)
            rating = bundleData.getString(StringConstants.VAP_AD_RATING)
        }
    }
}