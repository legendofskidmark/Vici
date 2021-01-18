package com.vici.vici.Activities

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vici.vici.Activities.MainActivity.Companion.db
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import kotlinx.android.synthetic.main.activity_newuser.*
import kotlinx.android.synthetic.main.activity_newuser.view.*

class NewUserActiviy: AppCompatActivity() {

    lateinit var name: String
    lateinit var phNo: String
    lateinit var address: String
    lateinit var currentLatLang: LatLng
    val TAG = "NEW_USER_ACTIVITY"
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newuser)
        this.setFinishOnTouchOutside(false)
//        configureSharedPrefsForNewUserActivity()
    }

    private fun configureSharedPrefsForNewUserActivity() {
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

    override fun onStart() {
        super.onStart()

        val editText = findViewById<EditText>(R.id.item_edittext)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && s.toString().last() == ',') {
                    val lastIndex = s?.length
                    val itemName = s.toString().substring(0, lastIndex!! - 1)
                    val newChip = Chip(this@NewUserActiviy)
                    newChip.text = itemName
                    lending_items_chip_group.addView(newChip)
                    s.clear()
                    editText.text.clear()
                    popUpToCollectItemDetails(itemName)

//                    done_newuser_button.isEnabled = true
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        done_newuser_button.setOnClickListener {
            if (ifAllDetailsAreFilledThenAddtoDB()) {
                configureSharedPrefsForNewUserActivity()
//                val intent = Intent(this, MapsActivity::class.java)
//                startActivity(intent)
//                finish()
            }
        }
    }


    private fun ifAllDetailsAreFilledThenAddtoDB(): Boolean {
        name = name_textview.editText?.text.toString()
        phNo = phone_textview.editText?.text.toString()
        address = address_newuser_textview.editText?.text.toString()

        //if there are any items, if they wanna lend, add em to DB as well

        if (name.isEmpty() || phNo.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_LONG).show()
            return false
        } else {
            val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(this, StringConstants.SHARED_PREF_FILE_NAME)
            val userEmailID = sharedPref.getString(StringConstants.USER_EMAIL_ID, "").toString()

            db.collection(StringConstants.UsersDBName).whereEqualTo(StringConstants.USER_EMAIL_ID, userEmailID).get().addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    Log.d(TAG, task.result.toList().toString())
//                    task.result.toList()[0].data["emailID"]
                    if (task.result.toList().size > 0) {
                        //do nothing
                    } else {
                        getDeviceLocationAndAddtoDB()
                    }
                }
            }
        }
        return true
    }

    private fun popUpToCollectItemDetails(itemName: String) {
        val itemDetailCollectPopup = ItemDetailCollectPopupView(this, itemName)
        itemDetailCollectPopup.show(supportFragmentManager, "Item_Detail_Collect")
        itemDetailCollectPopup.isCancelable = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun getDeviceLocationAndAddtoDB() {
        val location = if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

                val permissions = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (ContextCompat.checkSelfPermission(this.applicationContext, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this.applicationContext, COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
                    }
                } else {
                    ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
                }
//            return
        } else {

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return
                        }
                        i++
                    }

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return;
                    }
                    MapsActivity.mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                    MapsActivity.mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener(object : OnCompleteListener<Location> {
                        override fun onComplete(task: Task<Location>) {
                            if (task.isSuccessful) {
                                val currentLocation: Location? = task.result
                                if (currentLocation != null) {
                                    currentLatLang = LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                                    val user = hashMapOf(
                                        StringConstants.NAME to name,
                                        StringConstants.MOBILE to phNo,
                                        StringConstants.ADDRESS to address,
                                        StringConstants.LAT_LONG to currentLocation.latitude.toString() + "," + currentLocation.longitude.toString()
                                    )

                                    db.collection(StringConstants.UsersDBName).document(MainActivity.userEmailID).set(user).addOnSuccessListener { task ->
                                        Log.d(TAG, "Users DB Task Success")
                                    }.addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }

                                    val intent = Intent(applicationContext, MapsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                print("unable to get current location")
                            }
                        }
                    })
                }
            }
        }
    }
}
