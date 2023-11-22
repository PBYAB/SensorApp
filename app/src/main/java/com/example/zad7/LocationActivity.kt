package com.example.zad7
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class LocationActivity : AppCompatActivity() {
    private lateinit var getLocationButton: Button
    private lateinit var getLocationTextView: TextView
    private lateinit var lastLocation : Location
    private lateinit var locationTextView : TextView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var addressTextView: TextView
    private lateinit var getAddressButton: Button

        companion object {
            private const val REQUEST_LOCATION_PERMISSION = 1
            private const val TAG = "LocationActivity"
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_activity)

        getLocationButton = findViewById(R.id.get_location_button)
        getLocationTextView = findViewById(R.id.get_location_text_view)
        locationTextView = findViewById(R.id.location_text_view)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        addressTextView = findViewById(R.id.textview_address)
        getAddressButton = findViewById(R.id.get_address_button)


        getLocationButton.setOnClickListener {
            getLocation()
        }

        getAddressButton.setOnClickListener {
            executeGeocoding()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if(location != null) {
                    lastLocation = location
                    locationTextView.text = getString(
                        R.string.location_text,
                        location.latitude,
                        location.longitude,
                        location.time
                    )
                } else {
                    locationTextView.text = getString(R.string.no_location)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                }
                else {
                    Toast.makeText(this,
                        R.string.location_permission_denied,
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun locationGeocoding(context : Context, location : Location) : String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address> = emptyList()
        var resultMessage = ""

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1)!!
        } catch (ioException: Exception) {
            resultMessage = context.getString(R.string.service_not_available)
            Log.e(TAG, resultMessage, ioException)
        }

        if(addresses.isEmpty()) {
            if (resultMessage.isEmpty()) {
                resultMessage = context.getString(R.string.no_address_found)
                Log.e(TAG, resultMessage)
            }
        } else {
            val address = addresses[0]
            val addressParts = ArrayList<String>()

                for (i in 0..address.maxAddressLineIndex) {
                    addressParts.add(address.getAddressLine(i))
            }
            resultMessage = addressParts.joinToString("\n")
        }
        return resultMessage
    }


    private fun executeGeocoding() {
        if (::lastLocation.isInitialized) {
            val executor = Executors.newSingleThreadExecutor()
            val returnedAddress = executor.submit<String> { locationGeocoding(this, lastLocation) } as Future<String>

            try {
                val result = returnedAddress.get()
                addressTextView.text = getString(R.string.address_text,
                    result,
                    System.currentTimeMillis())
            } catch (e: ExecutionException) {
                Log.e(TAG, e.message.toString(), e)
                Thread.currentThread().interrupt()
            } catch (e: InterruptedException) {
                Log.e(TAG, e.message.toString(), e)
                Thread.currentThread().interrupt()
            }
        }
    }

}