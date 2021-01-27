package com.vici.vici.Activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.vici.vici.R

//class FilterPopupView: AppCompatDialogFragment() {
//
//    interface FilterAppliedListener {
//        fun applyFilter(selectedOption: String)
//    }
//
//    lateinit var filterAppliedListener: FilterAppliedListener
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val alertDialog = AlertDialog.Builder(activity).create()
//        val inflater = activity!!.layoutInflater
//        val view: View = inflater.inflate(R.layout.filter_popupview, null)
//        val radioGroup = view.findViewById<RadioGroup>(R.id.FilterRadioGroup)
//
//        alertDialog.setView(view)
//
////        builder.setView(view).setTitle("Filters")
////            .setPositiveButton("Apply") { dialogInterface, i ->
////                val selectedOption = radioGroup.checkedRadioButtonId
////                if (selectedOption == -1) {
////                    Toast.makeText(context, "Please choose a option", Toast.LENGTH_LONG).show()
////                } else {
////                    val selectedRadioButton = view.findViewById<RadioButton>(selectedOption)
////                    filterAppliedListener.applyFilter(selectedRadioButton.text.toString())
////                }
////            }
//
//        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
//            val selectedOption = i
//            val selectedRadioButton = view.findViewById<RadioButton>(selectedOption)
//            filterAppliedListener.applyFilter(selectedRadioButton.text.toString())
//            alertDialog.dismiss()
//        }
//
//        return alertDialog
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        try {
//            filterAppliedListener = context as FilterAppliedListener
//        } catch (e: ClassCastException) {
//            e.printStackTrace()
//        }
//    }
//}