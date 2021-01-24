package com.vici.vici.Activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.errors.InstantiationErrorException
import com.here.sdk.routing.*
import com.vici.vici.Activities.MainActivity.Companion.db
import com.vici.vici.Activities.MapsActivity.Companion.mFusedLocationProviderClient
import com.vici.vici.Adapters.AdsResultAdapter
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.Utility
import com.vici.vici.models.AdModel
import kotlinx.android.synthetic.main.activity_browsepage.*
import kotlinx.android.synthetic.main.filter_mapview_view.view.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


//////// <<<<<< GET


class BrowseActivity : AppCompatActivity(), FilterPopupView.FilterAppliedListener {

    val halfHourInMillisec: Long = 1800000
    val tenMininMillisec: Long = 600000
    val oneMinInMillisec: Long = 60000
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
    var shouldDisplayEndDateSelector: Boolean = true
    lateinit var calender: Calendar
    lateinit var startCalendar: Calendar
    lateinit var endCalendar: Calendar
    var dummyResponse = ArrayList<AdModel>()
    lateinit var currentLatLang: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browsepage)
        Utility.hideActionBar(supportActionBar, window)

        browse_page_searchbar.setStartIconOnClickListener { finish() }
        browse_page_searchbar.setEndIconOnClickListener { finish() }

        getDeviceLocation()
        addTextToSearchBar()
        addListenerToSearchBar()

        calender = Calendar.getInstance()

        configureDateTimePicker()
//        configureResultsRecyclerView()
        configureBottomButtons()
    }

    private fun configureBottomButtons() {
        filter_mapview_buttons.filter_button.setOnClickListener {
            val filterPopupView = FilterPopupView()
            filterPopupView.show(supportFragmentManager, "Filter_Menu")
        }

        filter_mapview_buttons.map_view_button.setOnClickListener {
            val intent = Intent(this, MapsViewActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun applyFilter(selectedOption: String) {
        if (getString(R.string.distance_string) == selectedOption) {
            val sortedDistList = dummyResponse.sortedWith(compareBy({it.distance}))
            dummyResponse.clear()
            dummyResponse.addAll(sortedDistList)
        } else if (getString(R.string.price_string) == selectedOption) {
            val sortedPriceList = dummyResponse.sortedWith(compareBy({it.price}))
            dummyResponse.clear()
            dummyResponse.addAll(sortedPriceList)
        }
        ads_recyclerview.adapter = AdsResultAdapter(this, dummyResponse)
        (ads_recyclerview.adapter as AdsResultAdapter).notifyDataSetChanged()
    }

    private fun configureResultsRecyclerView() {

        val url = ArrayList<String>()
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")
        url.add("https://d2e111jq13me73.cloudfront.net/sites/default/files/styles/review_gallery_thumbnail_large/public/screenshots/csm-tv/the-flintstones-ss1.jpg?itok=mq5Z7hG3")


        val bundle = intent.extras
        val ads = bundle?.getSerializable(StringConstants.CLICKED_SEARCH_RESULT_BUNDLE) as HashMap<String, ArrayList<AdModel>>
        val brandKey = ads.keys.toList().first()
        var adsAdapterArray = ArrayList<AdModel>()

        for (item in ads[brandKey]!!) {
            configureAdModel(item) { addressOfUser ->
                try {
                    val s = Waypoint(GeoCoordinates(currentLatLang.latitude, currentLatLang.longitude))
                    val latLong = item.latLong?.split(",")
                    val d = Waypoint(GeoCoordinates(latLong?.first().toString().toDouble(), latLong?.last().toString().toDouble()))
                    val waypoints = ArrayList<Waypoint>()
                    waypoints.add(s)
                    waypoints.add(d)

                    val routingEngine = RoutingEngine()
                    routingEngine.calculateRoute(
                        waypoints,
                        CarOptions(),
                        CalculateRouteCallback() { routingError: RoutingError?, routes: MutableList<Route>? ->
                            if (routingError == null) {
                                val route: Route = routes!!.get(0)
                                val distanceInkms = routes[0].lengthInMeters/1000.0
                                item.distance = distanceInkms
                            } else {
                                item.distance = -1.0
                            }
                            item.imgUrls = url
                            adsAdapterArray.add(item)
                            if (item == ads[brandKey]!!.last()) {
                                ads_recyclerview.layoutManager = LinearLayoutManager(this)
                                ads_recyclerview.adapter = AdsResultAdapter(this, adsAdapterArray)
                                (ads_recyclerview.adapter as AdsResultAdapter).notifyDataSetChanged()
                            }
                        }
                    )
                } catch (e: InstantiationErrorException) {
                    throw RuntimeException("Initialization of RoutingEngine failed: " + e.error.name)
                }


            }
        }
    }

    private fun configureAdModel(item: AdModel, getAddressOfOwnerCallback: (email: String) -> Unit) {
        db.collection(StringConstants.UsersDBName).document(item.emailID.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val address = task.result[StringConstants.ADDRESS].toString()
                getAddressOfOwnerCallback(address)
            }
        }
    }

    private fun configureDateTimePicker() {

        setupDefaultTimings()

        date_time_selector.findViewById<LinearLayout>(R.id.start_date_time).setOnClickListener {
            showStartDateSelector()
        }

        date_time_selector.findViewById<LinearLayout>(R.id.end_date_time).setOnClickListener {
            showEndDateSelector()
        }
    }

    private fun setupDefaultTimings() {
        calender.timeInMillis = calender.timeInMillis + tenMininMillisec
        var year = calender.get(Calendar.YEAR)
        var month = calender.get(Calendar.MONTH)
        var day = calender.get(Calendar.DAY_OF_MONTH)
        var hour = calender.get(Calendar.HOUR_OF_DAY)
        var min = calender.get(Calendar.MINUTE)
        changeStartDateTimTextViews(day, month, year, hour, min)
        cYear = year
        cMonth = month
        cDay = day
        cHour = hour
        cMinute = min

        var currentCalendar = calender.clone() as Calendar
        startCalendar = currentCalendar
        currentCalendar.timeInMillis = currentCalendar.timeInMillis + halfHourInMillisec

        year = currentCalendar.get(Calendar.YEAR)
        month = currentCalendar.get(Calendar.MONTH)
        day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        hour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        min = currentCalendar.get(Calendar.MINUTE)
        endCalendar = currentCalendar
        changeEndDateTimeTextViews(day, month, year, hour, min)

    }

    private fun changeEndDateTimeTextViews(day: Int, month: Int, year: Int, hour: Int, min: Int) {
        date_time_selector.findViewById<LinearLayout>(R.id.end_date_time).findViewById<TextView>(R.id.end_date).text = day.toString().padStart(2, '0') + "/" + (month + 1).toString().padStart(2, '0') + "/" + year.toString().padStart(2, '0')
        date_time_selector.findViewById<LinearLayout>(R.id.end_date_time).findViewById<TextView>(R.id.end_time).text = hour.toString().padStart(2, '0') + ":" + min.toString().padStart(2, '0')
    }

    private fun changeStartDateTimTextViews(day: Int, month: Int, year: Int, hour: Int, min: Int) {
        date_time_selector.findViewById<LinearLayout>(R.id.start_date_time).findViewById<TextView>(R.id.from_date).text = day.toString().padStart(2, '0') + "/" + (month + 1).toString().padStart(2, '0') + "/" + year.toString().padStart(2, '0')
        date_time_selector.findViewById<LinearLayout>(R.id.start_date_time).findViewById<TextView>(R.id.from_time).text = hour.toString().padStart(2, '0') + ":" + min.toString().padStart(2, '0')
    }

    private fun addListenerToSearchBar() {
        browse_page_searchbar.editText?.setOnClickListener {
            val intent = intent.putExtra(StringConstants.QUERIED_SEARCH_RESULT, browse_page_searchbar.editText?.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun addTextToSearchBar() {
        val searchedQuery = intent.getStringExtra(StringConstants.CLICKED_SEARCH_RESULT)
        if (searchedQuery != null) browse_page_searchbar.editText?.setText(searchedQuery)
        browse_page_searchbar.editText?.setSelection(searchedQuery!!.length)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun showStartDateSelector() {

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
        datePickerDialog.setTitle("Choose Start Date @" + cHour.toString().padStart(2, '0') + ":" + cMinute.toString().padStart(2, '0'))
        datePickerDialog.show()
    }

    private fun showStartTimeSelector() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            sHour = hour
            sMinute = minute

            cHour = sHour
            cMinute = cMinute

            calender.set(Calendar.HOUR_OF_DAY, sHour)
            calender.set(Calendar.MINUTE, sMinute)

            startCalendar = calender.clone() as Calendar
            if (calender.timeInMillis < Calendar.getInstance().timeInMillis) {
                Toast.makeText(this, "Please select some other time in future", Toast.LENGTH_SHORT).show()
                showStartTimeSelector()
            } else {
                if (startCalendar.timeInMillis + halfHourInMillisec > endCalendar.timeInMillis ) {
                    Toast.makeText(this, "The time you've selected is more than the end time", Toast.LENGTH_LONG).show()
                    changeStartDateTimTextViews(sDay, sMonth, sYear, sHour, sMinute)
                    showEndDateSelector()
                } else {
                    // correct case
                    changeStartDateTimTextViews(sDay, sMonth, sYear, sHour, sMinute)
                }
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
            date_time_selector.findViewById<LinearLayout>(R.id.end_date_time).findViewById<TextView>(R.id.end_date).text = eDay.toString().padStart(2, '0') + "/" + (eMonth + 1).toString().padStart(2, '0') + "/" + eYear.toString().padStart(2, '0')
            showEndTimeSelector()
        }, cYear, cMonth, cDay)

        datePickerDialog.datePicker.minDate = calender.timeInMillis - 1000

        val startDateFromDpd = date_time_selector.findViewById<LinearLayout>(R.id.start_date_time).findViewById<TextView>(R.id.from_date).text
        datePickerDialog.setTitle("From : " + startDateFromDpd.toString() + "\nChoose End Date\n")
        datePickerDialog.setOnCancelListener {
            if (endCalendar.timeInMillis < startCalendar.timeInMillis) {
                showEndDateSelector()
            }
        }
        datePickerDialog.show()
    }

    private fun showEndTimeSelector() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener {view, hour, minute ->
            eHour = hour
            eMinute = minute

            calender.set(Calendar.HOUR_OF_DAY, eHour)
            calender.set(Calendar.MINUTE, eMinute)

            endCalendar = calender.clone() as Calendar

            if (endCalendar.timeInMillis < startCalendar.timeInMillis + halfHourInMillisec) {
                Toast.makeText(this, "Please select Future time with at least 30 min diff", Toast.LENGTH_SHORT).show()
                showEndTimeSelector()
            } else {
                changeEndDateTimeTextViews(eDay, eMonth, eYear, eHour, eMinute)
            }
        }, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), true)

        var displayString = "Choose End Time"
        if (checkForSameDay()) {
            val endSelectableTimeRange = startCalendar.timeInMillis + halfHourInMillisec
            val calenderTemp = Calendar.getInstance()
            calenderTemp.timeInMillis = endSelectableTimeRange
            val hour = calenderTemp.get(Calendar.HOUR_OF_DAY)
            val minute = calenderTemp.get(Calendar.MINUTE)

            displayString = "Pick time after " + hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0') + "\n" + displayString
        }
        timePickerDialog.setTitle(displayString)
        timePickerDialog.setOnCancelListener {
            if (endCalendar.timeInMillis < startCalendar.timeInMillis + halfHourInMillisec) {
                Toast.makeText(this, "Please select Future time with at least 30 min diff", Toast.LENGTH_SHORT).show()
                showEndTimeSelector()
            }
        }
        timePickerDialog.show()
    }

    private fun checkForSameDay(): Boolean {
        if (startCalendar.get(Calendar.DAY_OF_MONTH) == endCalendar.get(Calendar.DAY_OF_MONTH) &&
            startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH) &&
            startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)) return true

        return false
    }

    private fun getDeviceLocation() {
        val location = if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {

        }
            mFusedLocationProviderClient!!.lastLocation.addOnCompleteListener(object : OnCompleteListener<Location> {
            override fun onComplete(task: Task<Location>) {
                if (task.isSuccessful) {
                    val currentLocation: Location? = task.result as Location?
                    if (currentLocation != null) {
                        currentLatLang = LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                        configureResultsRecyclerView()
                    }
                } else {
                    print("unable to get current location")
                }
            }
        })
    }
}