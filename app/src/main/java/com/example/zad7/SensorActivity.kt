package com.example.zad7

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SensorActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorList: List<Sensor>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SensorAdapter
    private var subtitleVisible = false

    companion object{
        private const val KEY_SUBTITLE_VISIBLE = "Subtitle is visible"
        const val KEY_EXTRA_SENSOR_INDEX = "Sensor index"
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_SUBTITLE_VISIBLE, subtitleVisible)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sensor_activity)
        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(KEY_SUBTITLE_VISIBLE);
        }

        recyclerView = findViewById(R.id.sensor_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)

        adapter = SensorAdapter(sensorList)
        recyclerView.adapter = adapter

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.fragment_sensor_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.show_subtitle -> {
                subtitleVisible = !subtitleVisible
                invalidateOptionsMenu()
                updateSubtitle()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSubtitle() {
        var subtitle: String? = null
        if (subtitleVisible) subtitle = getString(R.string.sensor_count, sensorList.size)
        supportActionBar!!.subtitle = subtitle
    }

    class SensorHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.sensor_list_item, parent, false)),
        View.OnClickListener, View.OnLongClickListener {

        private val sensorIconImageView: ImageView = itemView.findViewById(R.id.sensor_item_image)
        private val sensorNameTextView: TextView = itemView.findViewById(R.id.sensor_item_name)
        private lateinit var sensor: Sensor

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        fun bind(sensor: Sensor) {
            this.sensor = sensor
            sensorIconImageView.setImageResource(R.drawable.ic_sensor_image)
            sensorNameTextView.text = sensor.name

            if (sensor.type == Sensor.TYPE_LIGHT || sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                itemView.setOnClickListener(this)
                sensorNameTextView.setTextColor(itemView.resources.getColor(R.color.red, null))
            } else if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                itemView.setOnClickListener {
                    val context = itemView.context
                    val intent = Intent(context, LocationActivity::class.java)
                    context.startActivity(intent)
                }
                sensorNameTextView.setTextColor(itemView.resources.getColor(R.color.blue, null))
            } else {
                itemView.setOnClickListener(null)
            }
        }

        override fun onClick(v: View) {
            val context = itemView.context
            val intent = Intent(context, SensorDetailsActivity::class.java)
            intent.putExtra(KEY_EXTRA_SENSOR_INDEX, sensor.type)
            context.startActivity(intent)
        }

        override fun onLongClick(v: View): Boolean {
            showSensorDetailsDialog()
            return true
        }

        private fun showSensorDetailsDialog() {
            val context = itemView.context
            val dialogFragment = SensorDetailsDialogFragment.newInstance(sensor.type)
            dialogFragment.show((context as AppCompatActivity).supportFragmentManager, "SENSOR_DETAILS_DIALOG")
        }
    }


    class SensorAdapter(private val sensors: List<Sensor>) :
        RecyclerView.Adapter<SensorHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return SensorHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: SensorHolder, position: Int) {
            val sensor = sensors[position]
            holder.bind(sensor)
        }

        override fun getItemCount(): Int {
            return sensors.size
        }
    }

}
