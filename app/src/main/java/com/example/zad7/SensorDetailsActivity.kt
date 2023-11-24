package com.example.zad7

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.zad7.databinding.SensorDetailsActivityBinding


class SensorDetailsActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var sensorLight: Sensor? = null
    private var sensorTemperature: Sensor? = null

    private lateinit var binding: SensorDetailsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SensorDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (sensorLight == null) {
            binding.sensorLightLabel.text = getString(R.string.missing_sensor)
        }

        if (sensorTemperature == null) {
            binding.sensorTemperatureLabel.text = getString(R.string.missing_sensor)
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
            Sensor.TYPE_LIGHT -> binding.sensorLightLabel.text = getString(R.string.Light_sensor_Label, currentValue)
            Sensor.TYPE_AMBIENT_TEMPERATURE -> binding.sensorTemperatureLabel.text =
                getString(R.string.Temperature_sensor_Label, currentValue)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SensorDetailsActivity", "Accuracy changed: $accuracy")
    }

    private fun displaySensorInformation(sensorType: Int) {
        when (sensorType) {
            Sensor.TYPE_LIGHT -> {
                binding.sensorTemperatureLabel.visibility = TextView.GONE
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                binding.sensorLightLabel.visibility = TextView.GONE
            }
        }
    }
}
