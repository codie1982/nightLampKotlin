package com.grnt.nightlampkotlin.di.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grnt.nightlampkotlin.BaseActivity
import com.grnt.nightlampkotlin.R

class StepOne : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_one)
    }
}