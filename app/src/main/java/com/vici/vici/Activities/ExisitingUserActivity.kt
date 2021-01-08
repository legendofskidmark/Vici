package com.vici.vici.Activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility

class ExisitingUserActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_exisitinguser)

        val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(applicationContext, StringConstants.SHARED_PREF_FILE_NAME)
//            getSharedPreferences(StringConstants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)
        val spEditor = sharedPref.edit()

        if (sharedPref.contains(StringConstants.FILLED_THE_FORM)) {
            // already filled

        } else {
            // first time
            spEditor.putBoolean(StringConstants.FILLED_THE_FORM, true)
        }

        spEditor.apply()
    }
}