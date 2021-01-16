package com.vici.vici.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
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

    val TAG = "NEW_USER_ACTIVITY"

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
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun ifAllDetailsAreFilledThenAddtoDB(): Boolean {
        val name = name_textview.editText?.text.toString()
        val phNo = phone_textview.editText?.text.toString()
        val address = address_newuser_textview.editText?.text.toString()

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
                        val user = hashMapOf(
                            StringConstants.NAME to name,
                            StringConstants.MOBILE to phNo,
                            StringConstants.ADDRESS to address
                        )

                        db.collection(StringConstants.UsersDBName).add(user).addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }.addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
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
}
