package com.example.zad7
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.zad7.databinding.LocationActivityBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationActivity : AppCompatActivity() {

    private lateinit var lastLocation : Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: LocationActivityBinding


        companion object {
            private const val REQUEST_LOCATION_PERMISSION = 1
            private const val TAG = "LocationActivity"
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.getLocationButton.setOnClickListener {
            getLocation()
        }

        binding.getAddressButton.setOnClickListener {
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
                    binding.locationTextView.text = getString(
                        R.string.location_text,
                        location.latitude,
                        location.longitude,
                        location.time
                    )
                } else {
                    binding.locationTextView.text = getString(R.string.no_location)
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



    private fun executeGeocoding() = CoroutineScope(Dispatchers.IO).launch {
        if (::lastLocation.isInitialized) {
            try {
                val result = async { locationGeocoding(this@LocationActivity, lastLocation) }.await()
                withContext(Dispatchers.Main) {
                    binding.textviewAddress.text = getString(R.string.address_text,
                        result,
                        System.currentTimeMillis())
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString(), e)
            }
        }
    }

}