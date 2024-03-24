package com.grnt.nightlampkotlin

import android.app.Application
import android.content.Context
import com.grnt.nightlampkotlin.di.DependencyInjection
import com.grnt.nightlampkotlin.di.DependencyInjectionImplementation

class App :Application() {
    val BASEURL = "http://192.168.1.82"
    val di: DependencyInjection by lazy {
        DependencyInjectionImplementation(BASEURL)
    }
}
val Context.di: DependencyInjection
    get() = (this.applicationContext as App).di