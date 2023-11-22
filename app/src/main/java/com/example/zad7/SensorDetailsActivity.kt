package com.example.zad7

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SensorDetailsActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var sensorLight: Sensor? = null
    private var sensorTemperature: Sensor? = null
    private lateinit var sensorLightTextView: TextView
    private lateinit var sensorTemperatureTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sensor_details_activity)

        sensorLightTextView = findViewById(R.id.sensor_Light_Label)
        sensorTemperatureTextView = findViewById(R.id.sensor_Temperature_Label)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (sensorLight == null) {
            sensorLightTextView.text = getString(R.string.missing_sensor)
        }

        if (sensorTemperature == null) {
            sensorTemperatureTextView.text = getString(R.string.missing_sensor)
        }

        val sensorType = intent.getIntExtra(SensorActivity.KEY_EXTRA_SENSOR_INDEX, -1)
        displaySensorInformation(sensorType)
    }

    override fun onStart() {
        super.onStart()

        sensorLight?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        sensorTemperature?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onStop() {
        super.onStop()

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val sensorType = event?.sensor?.type
        val currentValue = event?.values?.get(0)

        when (sensorType) {
            Sensor.TYPE_LIGHT -> sensorLightTextView.text = getString(R.string.Light_sensor_Label, currentValue)
            Sensor.TYPE_AMBIENT_TEMPERATURE -> sensorTemperatureTextView.text =
                getString(R.string.Temperature_sensor_Label, currentValue)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SensorDetailsActivity", "Accuracy changed: $accuracy")
    }

    private fun displaySensorInformation(sensorType: Int) {
        when (sensorType) {
            Sensor.TYPE_LIGHT -> {
                sensorTemperatureTextView.visibility = TextView.GONE
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                sensorLightTextView.visibility = TextView.GONE
            }
        }
    }
}
