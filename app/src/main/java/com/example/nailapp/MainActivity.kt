package com.example.nailapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.nailapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nixsensor.universalsdk.IDeviceScanner

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    companion object {
        // Define a constant value of your choice here
        const val PERMISSION_REQUEST_BLUETOOTH = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: ConstraintLayout = binding.drawerLayout
        val navView: BottomNavigationView = binding.bottomNav
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        navView.setupWithNavController(navController)

        checkAndRequestBluetoothPermissions()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkAndRequestBluetoothPermissions() {
        if (!IDeviceScanner.isBluetoothPermissionGranted(this)) {
            IDeviceScanner.requestBluetoothPermissions(this, PERMISSION_REQUEST_BLUETOOTH)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if all requested permissions have been granted
        var allGranted = true
        for (result in grantResults) allGranted =
            allGranted and (result == PackageManager.PERMISSION_GRANTED)

        when (requestCode) {
            PERMISSION_REQUEST_BLUETOOTH -> {
                if (allGranted) {
                    // All permissions granted, OK to use `DeviceScanner`
                    // ...
                } else {
                    // Handle permission denial
                    // ...
                }
            }
        }
    }
}
