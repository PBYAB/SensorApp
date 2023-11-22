package com.example.zad7

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment

class SensorDetailsDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_SENSOR = "ARG_SENSOR"

        fun newInstance(sensorType: Int): SensorDetailsDialogFragment {
            val fragment = SensorDetailsDialogFragment()
            val args = Bundle()
            args.putInt(ARG_SENSOR, sensorType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val sensorType = arguments?.getInt(ARG_SENSOR)
        val sensorManager = (activity as AppCompatActivity).getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorType?.let { sensorManager.getDefaultSensor(it) }
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Sensor Details")
                .setMessage("Vendor: ${sensor?.vendor}\n Maximum Range: ${sensor?.maximumRange}")
                .setNegativeButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

