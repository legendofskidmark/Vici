package com.vici.vici.Util

import android.content.Context
import com.vici.vici.Constants.StringConstants
import android.content.SharedPreferences
import android.content.ContextWrapper

object SharedPreferencesUtility {

    lateinit var sharedPrefRef: SharedPreferences

    fun openSharedPreferencesWith(mContext: Context, fileName: String): SharedPreferences {
        sharedPrefRef = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return sharedPrefRef
    }
}