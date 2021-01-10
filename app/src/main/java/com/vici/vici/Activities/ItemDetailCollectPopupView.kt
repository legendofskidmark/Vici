package com.vici.vici.Activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vici.vici.Activities.MainActivity.Companion.db
import com.vici.vici.Constants.StringConstants
import com.vici.vici.Constants.StringConstants.AGE
import com.vici.vici.Constants.StringConstants.BRAND
import com.vici.vici.Constants.StringConstants.FROM_USER
import com.vici.vici.Constants.StringConstants.IS_ALIVE
import com.vici.vici.Constants.StringConstants.ITEMS
import com.vici.vici.Constants.StringConstants.ITEM_DETAILS
import com.vici.vici.Constants.StringConstants.MODEL
import com.vici.vici.Constants.StringConstants.NAME
import com.vici.vici.Constants.StringConstants.ORGANIZED_GROUP
import com.vici.vici.Constants.StringConstants.PER_TIME
import com.vici.vici.Constants.StringConstants.PRICE
import com.vici.vici.Constants.StringConstants.PRODUCTS
import com.vici.vici.Constants.StringConstants.USER_EMAIL_ID
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import kotlinx.android.synthetic.main.new_item_collect_popupview.view.*


class ItemDetailCollectPopupView(val mContext: Context, val itemName: String): AppCompatDialogFragment(), AdapterView.OnItemSelectedListener {

    val mTAG = "ITEM_DETAIL_COLLECTION_POPUPVIEW"
    lateinit var perTimeSpinner: Spinner
    var perTimeOptionsList = arrayOf("Minute","Hour", "Day", "Month")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog = AlertDialog.Builder(activity).create()
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.new_item_collect_popupview, null)

        alertDialog.setView(view)
        alertDialog.setCancelable(false)

        view.done_itemDetails_button.setOnClickListener {
            val brand = view.brandName_editText.text.toString()
            val model = view.model_name_editText.text.toString()
            val age = view.age_editText.text.toString()
            val price = view.price_editText.text.toString()
            val perItem = view.per_time_spinner.selectedItem.toString()

            if (brand.isEmpty() || model.isEmpty() || age.isEmpty() || price.isEmpty()) {
                Toast.makeText(context, "Please enter all the details", Toast.LENGTH_LONG).show()
            } else {
                val sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(mContext, StringConstants.SHARED_PREF_FILE_NAME)
                val userEmailID = sharedPref.getString(USER_EMAIL_ID, "").toString()

                val itemDetails = hashMapOf(
                    USER_EMAIL_ID to userEmailID,
                    NAME to itemName,
                    BRAND to brand,
                    MODEL to model,
                    AGE to age,
                    PRICE to price,
                    PER_TIME to perItem
                )

                val userMailMappy = hashMapOf(
                    USER_EMAIL_ID to userEmailID
                )

                db.collection(ORGANIZED_GROUP).document(brand.toUpperCase()).collection(PRODUCTS).add(itemDetails).addOnSuccessListener { documentReference ->
                    db.collection(ITEM_DETAILS).document(documentReference.id).set(userMailMappy).addOnCompleteListener { task ->
                        if (task.isSuccessful) Log.d(mTAG, "Item Details added with ${documentReference.id} for ${userEmailID} succesfully")
                        else Log.d(mTAG, "Item Details failed to add into DB")
                    }
                }

                alertDialog.dismiss()
            }
        }


        val aa = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, perTimeOptionsList) }
        aa?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        perTimeSpinner = view.findViewById<Spinner>(R.id.per_time_spinner)
        perTimeSpinner.setAdapter(aa)

        perTimeSpinner.setOnItemSelectedListener(this)
        perTimeSpinner.setSelection(0)
        return alertDialog
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
        perTimeSpinner.setSelection(position)
    }


}