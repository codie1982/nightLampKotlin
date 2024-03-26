package com.grnt.nightlampkotlin.di.view.tab_main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.grnt.nightlampkotlin.R

class TabMainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        supportFragmentManager.commit {
            replace(R.id.main_frame_layout, ColorFragment())
            setReorderingAllowed(true)
            addToBackStack(null) // Name can be null
        }
        setBottomListener()
    }

    private fun setBottomListener() {
        bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menuColor -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_frame_layout, ColorFragment())
                        setReorderingAllowed(true)
                        addToBackStack(null) // Name can be null
                    }
                }
                R.id.menuCalender -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_frame_layout, WeeklyColorFragment())
                        setReorderingAllowed(true)
                        addToBackStack(null) // Name can be null
                    }
                }
                R.id.menuAnimation -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_frame_layout, AnimatedColorFragment())
                        setReorderingAllowed(true)
                        addToBackStack(null) // Name can be null
                    }
                }
            }

            return@setOnItemSelectedListener  true
        }
    }
}