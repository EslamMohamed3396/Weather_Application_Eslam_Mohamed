package com.eslam.mohamed.weatherapplication.ui.actvity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.eslam.mohamed.weatherapplication.R
import com.eslam.mohamed.weatherapplication.databinding.ActivityMainBinding
import com.eslam.mohamed.weatherapplication.ui.base.BaseActvitiy
import com.eslam.mohamed.weatherapplication.utilits.Constants
import com.eslam.mohamed.weatherapplication.utilits.DialogUtil
import com.eslam.mohamed.weatherapplication.utilits.Permissions
import com.eslam.mohamed.weatherapplication.utilits.Resource
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseActvitiy<ActivityMainBinding>() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView(R.layout.activity_main)
        initFusedLocation()
        initSearchEdit(binding?.searchInputLayout)
    }


    //region get location
    private fun initFusedLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun getLastLocation() {
        if (isServicesOk()) {
            if (checkGPSEnabled()) {
                if (!Permissions.checkLocationPermission(this)) {
                    mFusedLocationClient!!.lastLocation.addOnCompleteListener { task: Task<Location?> ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            setCurrentLatLong(location)
                        }
                    }.addOnFailureListener { e: Exception ->
                    }
                } else {
                    askLocationPermission(this)
                }
            } else {
                initAlertDialogForGPS(this)
            }
        }
    }

    //endregion

    //region check gps, ask permissions, services and gps is enabled
    private fun isServicesOk(): Boolean {
        val googleApi = GoogleApiAvailability.getInstance()
        val result = googleApi.isGooglePlayServicesAvailable(this)
        if (result == ConnectionResult.SUCCESS) {
            return true
        } else if (googleApi.isUserResolvableError(result)) {
            val dialog =
                googleApi.getErrorDialog(
                    this, result, Constants.ERROR_DIALOG_REQUEST
                ) { task: DialogInterface? ->
                    Toast.makeText(
                        this,
                        "Dialog is cancelled by User",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            dialog.show()
        } else {
            Toast.makeText(
                this,
                "Play services are required by this application",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    private fun checkGPSEnabled(): Boolean {
        val locationManager =
            (getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun initAlertDialogForGPS(activity: Activity) {
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(activity.resources.getString(R.string.permission))
            .setMessage(activity.resources.getString(R.string.gps_required))
            .setPositiveButton(
                activity.resources.getString(R.string.yes)
            ) { dialogInterface: DialogInterface?, i: Int ->
                val intent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, Constants.PERMISSIONS_REQUEST_ENABLE_GPS)
            }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun askLocationPermission(context: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


    //endregion

    //region request current loation

    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5000
        mLocationRequest.fastestInterval = 5000
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            setCurrentLatLong(mLastLocation)

            val currentLocation =
                mLastLocation.latitude.toString() + "," + mLastLocation.longitude.toString()
        }
    }

    private fun setCurrentLatLong(currentLatLong: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(
            currentLatLong.latitude,
            currentLatLong.longitude,
            1
        )
        val cityName: String = addresses[0].adminArea
        binding?.searchInputLayout?.editText?.hint = cityName
        initWeatherViewModel(cityName)

    }

    //endregion

    override fun onResume() {
        super.onResume()
        getLastLocation()
    }

    //region result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLastLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PERMISSIONS_REQUEST_ENABLE_GPS) {
            if (checkGPSEnabled()) {
                getLastLocation()
            }
        }
    }
    //endregion


    //region init view Model to get weather

    private fun initWeatherViewModel(cityName: String) {
        val viewModel = ViewModelProvider(this).get(MainActvityViewModel::class.java)
        viewModel.getWeather(cityName, resources.getString(R.string.key))
            .observe(this, { response ->
                when (response) {
                    is Resource.Loading -> {
                        DialogUtil.showDialog(this)
                    }
                    is Resource.Success -> {
                        DialogUtil.dismissDialog()
                        binding?.weather = response.data
                    }
                    is Resource.Error -> {
                        val message = response.message
                        DialogUtil.dismissDialog()
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }

                }
            })
    }

    //endregion


    //region search by city

    private fun initSearchEdit(inputLayout: TextInputLayout?) {
        Observable.create { emitter: ObservableEmitter<String?> ->
            inputLayout?.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.trim().isNotEmpty()) {
                        emitter.onNext(s.toString())
                    }
                }

                override fun afterTextChanged(s: Editable) {

                }
            })
        }.debounce(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { s: String? ->
                initWeatherViewModel(s.toString())
            }


    }


    //endregion


}