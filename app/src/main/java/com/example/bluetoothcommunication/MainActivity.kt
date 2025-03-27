package com.example.bluetoothcommunication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var btnShowPairedDevices: Button
    private lateinit var tvPairedDevices: TextView

    // Permission launcher for requesting Bluetooth permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showPairedDevices() // Permission granted, show paired devices
            } else {
                tvPairedDevices.text = "Permission denied. Cannot access Bluetooth devices."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnShowPairedDevices = findViewById(R.id.btnShowPairedDevices)
        tvPairedDevices = findViewById(R.id.tvPairedDevices)

        btnShowPairedDevices.setOnClickListener {
            checkBluetoothPermissionAndShowDevices()
        }
    }

    private fun checkBluetoothPermissionAndShowDevices() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
                showPairedDevices()
            }
            else -> {
                // Request permission using ActivityResultLauncher
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    private fun showPairedDevices() {
        if (bluetoothAdapter == null) {
            tvPairedDevices.text = "Bluetooth not supported on this device."
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            tvPairedDevices.text = "Please enable Bluetooth first."
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
            val deviceNames = pairedDevices?.joinToString("\n") { device -> device.name } ?: "No paired devices found"
            tvPairedDevices.text = deviceNames
        }
    }
}
