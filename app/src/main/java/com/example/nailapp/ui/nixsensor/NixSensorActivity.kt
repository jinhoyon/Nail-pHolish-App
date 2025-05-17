package com.example.nailapp.ui.nixsensor

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nailapp.databinding.ActivityNixsensorBinding
import com.example.nailapp.MainActivity
import com.nixsensor.universalsdk.CommandStatus
import com.nixsensor.universalsdk.DeviceCompat
import com.nixsensor.universalsdk.IDeviceCompat
import com.nixsensor.universalsdk.IDeviceScanner
import com.nixsensor.universalsdk.DeviceScanner
import com.nixsensor.universalsdk.DeviceStatus
import com.nixsensor.universalsdk.IColorData
import com.nixsensor.universalsdk.IMeasurementData
import com.nixsensor.universalsdk.OnDeviceResultListener
import com.nixsensor.universalsdk.ReferenceWhite
import com.nixsensor.universalsdk.ScanMode
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.core.graphics.ColorUtils
import com.example.nailapp.R
import android.widget.ArrayAdapter
import android.widget.EditText
import com.example.nailapp.NixDeviceManager


class NixSensorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNixsensorBinding
    private var recalledDevice: IDeviceCompat? = null

    private val phColors = mutableMapOf(
        "2.0" to intArrayOf(163, 95, 135),
//        "3.0" to intArrayOf(142, 100, 131),
//        "4.0" to intArrayOf(142, 100, 131),
        "5.0" to intArrayOf(145, 104, 140),
//        "6.0" to intArrayOf(137, 99, 133),
        "7.0" to intArrayOf(146, 123, 154),
//        "8.0" to intArrayOf(95, 104, 132),
        "9.0" to intArrayOf(111, 120, 152)
    )
    private lateinit var deviceListView: ListView
    private lateinit var progressBar: ProgressBar
    private val deviceList = mutableListOf<IDeviceCompat>()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityNixsensorBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        displayPHColors()
//        setupUI()
//        initializeScanner()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNixsensorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        displayPHColors()

        // Get previously connected device from manager
        recalledDevice = NixDeviceManager.connectedDevice

        if (NixDeviceManager.isConnected && recalledDevice != null) {
            updateButtonOnConnection()
            updateUIOnConnection()
            progressBar.visibility = View.GONE
            deviceListView.visibility = View.GONE
        } else {
            initializeScanner()
        }
    }


    private fun setupUI() {
        deviceListView = findViewById(R.id.deviceListView)
        progressBar = findViewById(R.id.progressBar)

        binding.buttonS.setOnClickListener { measureSensorData() }
        binding.buttonH.setOnClickListener { navigateToHome() }

        deviceListView.setOnItemClickListener { _, _, position, _ ->
            val selectedDevice = deviceList[position]
            recalledDevice = selectedDevice
            connectToDevice()
        }

//        findViewById<View>(R.id.button_edit_colors).setOnClickListener {
//            showUpdateColorDialog()
//        }
    }

    private fun initializeScanner() {
        progressBar.visibility = View.VISIBLE
        val scannerStateListener = object : IDeviceScanner.OnScannerStateChangeListener {
            override fun onScannerStarted(sender: IDeviceScanner) {
                Log.d(TAG, "Scanner started")
            }

            override fun onScannerStopped(sender: IDeviceScanner) {
                Log.d(TAG, "Scanner stopped")
                progressBar.visibility = View.GONE
            }
        }

        val deviceFoundListener = object : IDeviceScanner.OnDeviceFoundListener {
            override fun onScanResult(sender: IDeviceScanner, device: IDeviceCompat) {
                if (!isDeviceAlreadyDiscovered(device)) {
                    deviceList.add(device)
                    updateDeviceListView()
                    Log.d(TAG, "Found ${device.name} (${device.id}) at RSSI ${device.rssi}")
                } else {
                    Log.d(TAG, "Device ${device.name} (${device.id}) is already discovered")
                }
            }
        }

        val context: Context = applicationContext
        val scanner = DeviceScanner(context)
        scanner.setOnScannerStateChangeListener(scannerStateListener)
        scanner.start(listener = deviceFoundListener)
    }

    private fun updateDeviceListView() {
        runOnUiThread {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList.map { it.name })
            deviceListView.adapter = adapter
            deviceListView.visibility = View.VISIBLE
        }
    }

    private fun isDeviceAlreadyDiscovered(device: IDeviceCompat): Boolean {
        return deviceList.any { it.id == device.id }
    }
    private fun recallDevice() {
        val deviceId = "CB:1A:6C:5A:7F:82"
        val deviceName = "Nix Spectro 2"

        recalledDevice = DeviceCompat(applicationContext, deviceId, deviceName)
        connectToDevice()
    }

    private fun connectToDevice() {
        recalledDevice?.connect(object : IDeviceCompat.OnDeviceStateChangeListener {
            override fun onConnected(sender: IDeviceCompat) {
                NixDeviceManager.saveDevice(sender) // ✅ Save connected device
                showToast("Connected to ${sender.name}")
                updateButtonOnConnection()
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    deviceListView.visibility = View.GONE
                    binding.rgbTextView.text = ""
                }
            }

            override fun onDisconnected(sender: IDeviceCompat, status: DeviceStatus) {
                handleDisconnection(sender, status)
            }

            override fun onBatteryStateChanged(sender: IDeviceCompat, newState: Int) {
                Log.d("NixSensorActivity", "Battery state: $newState")
            }

            override fun onExtPowerStateChanged(sender: IDeviceCompat, newState: Boolean) {
                Log.d("NixSensorActivity", "External power state: $newState")
            }
        }) ?: Log.e("NixSensorActivity", "Recalled device is null, connection failed.")
    }

    private fun updateButtonOnConnection() {
        runOnUiThread {
            binding.buttonS.apply {
                text = "Analyze"
                isEnabled = true
                backgroundTintList = getColorStateList(R.color.purple_500)
            }
        }
    }

    private fun displayPHColors() {
        // Set the background color of each sample view
        phColors["2.0"]?.let { rgb ->
            binding.colorSample2.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
        }
        phColors["5.0"]?.let { rgb ->
            binding.colorSample5.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
        }
        phColors["7.0"]?.let { rgb ->
            binding.colorSample7.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
        }
        phColors["9.0"]?.let { rgb ->
            binding.colorSample9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
        }
//        phColors["7.0"]?.let { rgb ->
//            binding.colorSample8.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
//        }
//        phColors["8.0"]?.let { rgb ->
//            binding.colorSample9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
//        }
//        phColors["9.0"]?.let { rgb ->
//            binding.colorSample9.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
//        }
    }

    private fun updateUIOnConnection() {
        binding.rgbTextView.visibility = View.VISIBLE
        binding.colorSquare.visibility = View.VISIBLE
    }

    private fun measureSensorData() {
        recalledDevice?.measure(object : OnDeviceResultListener {
            override fun onDeviceResult(status: CommandStatus, measurements: Map<ScanMode, IMeasurementData>?) {
                if (status == CommandStatus.SUCCESS) {
                    measurements?.get(ScanMode.M2)?.let { measurementData ->
                        val colorData = measurementData.toColorData(ReferenceWhite.D50_2)
                        updateColorDisplay(colorData)
                        val predictedPH =  predictPH(colorData?.rgbValue ?: intArrayOf(0, 0, 0))
                        updatePHDisplay(predictedPH)
                    } ?: Log.e("NixSensorActivity", "Measurement data unavailable.")
                } else {
                    logMeasurementError(status)
                }
            }
        }) ?: Log.e("NixSensorActivity", "Device is not connected.")
    }

    private fun updatePHDisplay(predictedPH: String) {
        runOnUiThread {
            binding.predictionsTextView.text = "pH: $predictedPH"
            binding.predictionsTextView.visibility = View.VISIBLE
        }
    }

    private fun predictPH(rgbValue: IntArray): String {
        val nearestLab = phColors
        val distances = nearestLab.map { (ph, value) ->
            val distance = euclideanDistance(rgbValue, value)
            val labdistance = labDistance(rgbValue,value)
            Log.d(TAG, "RGB Distance to pH $ph: $distance")
            Log.d(TAG, "LAB Distance to pH $ph: $labdistance")
            ph to distance
        }

        return distances.minByOrNull { it.second }?.first ?: "Unknown"
    }

    private fun euclideanDistance(rgb1: IntArray, rgb2: IntArray): Double {
        return sqrt(
            (rgb1[0] - rgb2[0]).toDouble().pow(2) +
                    (rgb1[1] - rgb2[1]).toDouble().pow(2) +
                    (rgb1[2] - rgb2[2]).toDouble().pow(2)
        )
    }

    private fun labDistance(rgb1: IntArray, rgb2: IntArray): Double {
        val lab1 = DoubleArray(3)
        val lab2 = DoubleArray(3)

        // Convert RGB to LAB
        ColorUtils.RGBToLAB(rgb1[0], rgb1[1], rgb1[2], lab1)
        ColorUtils.RGBToLAB(rgb2[0], rgb2[1], rgb2[2], lab2)

        // Calculate the Euclidean distance in LAB space
        return sqrt(
            (lab1[0] - lab2[0]).toDouble().pow(2) +
                    (lab1[1] - lab2[1]).toDouble().pow(2) +
                    (lab1[2] - lab2[2]).toDouble().pow(2)
        )
    }

    private fun updateColorDisplay(colorData: IColorData?) {
        colorData?.let {
            updateUIOnConnection()
            val rgb = it.rgbValue
            runOnUiThread {
                // binding.rgbTextView.text = "RGB: (${rgb[0]}, ${rgb[1]}, ${rgb[2]})"
                Log.d(TAG, "RGB: (${rgb[0]}, ${rgb[1]}, ${rgb[2]})")
                binding.colorSquare.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
            }
        }
    }

    private fun logMeasurementError(status: CommandStatus) {
        when (status) {
            CommandStatus.ERROR_NOT_READY -> Log.e("NixSensorActivity", "Device not ready.")
            CommandStatus.ERROR_LOW_POWER -> Log.e("NixSensorActivity", "Low battery power.")
            else -> Log.e("NixSensorActivity", "Measurement error: $status")
        }
    }

    private fun handleDisconnection(sender: IDeviceCompat, status: DeviceStatus) {
        val message = "Disconnected: ${sender.name} Status: $status"
        Log.d("NixSensorActivity", message)
        showToast(message)
    }
    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showUpdateColorDialog() {
        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Set RGB Values")

        val dialogView = layoutInflater.inflate(R.layout.dialog_ph_color, null)
        builder.setView(dialogView)

        val pHEditText = dialogView.findViewById<EditText>(R.id.edit_ph_value)
        val redEditText = dialogView.findViewById<EditText>(R.id.edit_red_value)
        val greenEditText = dialogView.findViewById<EditText>(R.id.edit_green_value)
        val blueEditText = dialogView.findViewById<EditText>(R.id.edit_blue_value)

        builder.setPositiveButton("Save") { _, _ ->
            val ph = pHEditText.text.toString()
            val red = redEditText.text.toString().toIntOrNull() ?: 0
            val green = greenEditText.text.toString().toIntOrNull() ?: 0
            val blue = blueEditText.text.toString().toIntOrNull() ?: 0

            if (ph.isNotEmpty()) {
                phColors[ph] = intArrayOf(red, green, blue)
                displayPHColors()  // Update the UI to reflect changes
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}