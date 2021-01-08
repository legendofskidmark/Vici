package com.vici.vici.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vici.vici.R
import kotlinx.android.synthetic.main.activity_browse_test.*
import java.util.*

class BrowsePageTest: AppCompatActivity() {

    var cYear: Int = 0
    var cMonth: Int = 0
    var cDay: Int = 0
    var sYear: Int = 0
    var sMonth: Int = 0
    var sDay: Int = 0
    var cHour: Int = 0
    var cMinute: Int = 0
    var sHour: Int = 0
    var sMinute: Int = 0
    var eYear: Int = 0
    var eMonth: Int = 0
    var eDay: Int = 0
    var eHour: Int = 0
    var eMinute: Int = 0
    lateinit var calender: Calendar
    lateinit var startCalendar: Calendar
    lateinit var endCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_test)
        calender = Calendar.getInstance()
        showStartDateSelector()
    }

    private fun showStartDateSelector() {
        cYear = calender.get(Calendar.YEAR)
        cMonth = calender.get(Calendar.MONTH)
        cDay = calender.get(Calendar.DAY_OF_MONTH)
        cHour = calender.get(Calendar.HOUR_OF_DAY)
        cMinute = calender.get(Calendar.MINUTE)
        ///////////// Correct Date set to today

        do_it.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, day ->
                sYear = year
                sMonth = month
                sDay = day

                calender.set(Calendar.YEAR, sYear)
                calender.set(Calendar.MONTH, sMonth)
                calender.set(Calendar.DAY_OF_MONTH, sDay)

                val selectedDate = sDay.toString() + ":" + sMonth.toString() + ":" + sYear.toString()
                Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show()
                showStartTimeSelector()
            }, cYear, cMonth, cDay)

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.setTitle("Choose start Date")
            datePickerDialog.show()
        }
    }

    private fun showStartTimeSelector() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            sHour = hour
            sMinute = minute

            calender.set(Calendar.HOUR_OF_DAY, sHour)
            calender.set(Calendar.MINUTE, sMinute)

            startCalendar = calender.clone() as Calendar
            if (calender.timeInMillis < Calendar.getInstance().timeInMillis) {
                Toast.makeText(this, "Please select future time", Toast.LENGTH_SHORT).show()
                showStartTimeSelector()
            } else {
                showEndDateSelector()
            }

        }, cHour, cMinute, true)

        timePickerDialog.setTitle("Choose Start Time")
        timePickerDialog.show()
    }

    private fun showEndDateSelector() {

        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, day ->
            eYear = year
            eMonth = month
            eDay = day
            val selectedDate = eDay.toString() + ":" + eMonth.toString() + ":" + eYear.toString()

            calender.set(Calendar.YEAR, eYear)
            calender.set(Calendar.MONTH, eMonth)
            calender.set(Calendar.DAY_OF_MONTH, eDay)

            Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show()
            showEndTimeSelector()
        }, cYear, cMonth, cDay)

        datePickerDialog.datePicker.minDate = calender.timeInMillis - 1000
        datePickerDialog.setTitle("Choose End Date")
        datePickerDialog.show()
    }

    private fun showEndTimeSelector() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener {view, hour, minute ->
            eHour = hour
            eMinute = minute

            calender.set(Calendar.HOUR_OF_DAY, eHour)
            calender.set(Calendar.MINUTE, eMinute)

            endCalendar = calender.clone() as Calendar

            if (endCalendar.timeInMillis < startCalendar.timeInMillis + 600000) {
                Toast.makeText(this, "Please select Future time with at least ten min diff", Toast.LENGTH_SHORT).show()
                showEndTimeSelector()
            } else {
                start_date_tv.text = sDay.toString() + ":" + sMonth.toString() + ":" + sYear.toString()
                start_time_tv.text = sHour.toString() + ":" + sMinute.toString()

                end_date_tv.text = eDay.toString() + ":" + eMonth.toString() + ":" + eYear.toString()
                end_time_tv.text = eHour.toString() + ":" + eMinute.toString()
            }
        }, cHour, cMinute, true)

        timePickerDialog.setTitle("Choose End Time")
        timePickerDialog.show()
    }
}