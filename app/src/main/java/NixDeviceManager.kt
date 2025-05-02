package com.example.nailapp

import com.nixsensor.universalsdk.IDeviceCompat

object NixDeviceManager {
    var connectedDevice: IDeviceCompat? = null
    var isConnected: Boolean = false

    fun saveDevice(device: IDeviceCompat) {
        connectedDevice = device
        isConnected = true
    }

    fun clearDevice() {
        connectedDevice = null
        isConnected = false
    }
}
