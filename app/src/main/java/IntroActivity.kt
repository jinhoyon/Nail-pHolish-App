package com.example.nailapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.nailapp.ui.nixsensor.NixSensorActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val myButton = findViewById<Button>(R.id.toMainButton)

        myButton.setOnClickListener {
            val intent = Intent(this, NixSensorActivity::class.java)
            startActivity(intent)
            finish() // Close IntroActivity so the user can't go back to it
        }
    }
}