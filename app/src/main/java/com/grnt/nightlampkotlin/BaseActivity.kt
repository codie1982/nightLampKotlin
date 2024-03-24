package com.grnt.nightlampkotlin

import androidx.appcompat.app.AppCompatActivity
import com.grnt.nightlampkotlin.model.LambRepository

open class BaseActivity:AppCompatActivity() {
    var repository: LambRepository? = null
    init {
        di.setBaseURL("http://192.168.4.1")
        repository = di.repository
    }
}