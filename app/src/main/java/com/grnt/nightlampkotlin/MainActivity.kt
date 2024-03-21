package com.grnt.nightlampkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var btnLedOpen: Button
    lateinit var btnLedClose:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLedOpen = findViewById(R.id.btnLedOpen)
        btnLedClose = findViewById(R.id.btnLedClose)

    }
}