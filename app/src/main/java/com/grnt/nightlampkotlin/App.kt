package com.grnt.nightlampkotlin

import android.app.Application
import android.content.Context
import com.grnt.nightlampkotlin.di.DependencyInjection
import com.grnt.nightlampkotlin.di.DependencyInjectionImplementation

class App :Application() {
    val di: DependencyInjection by lazy {
        DependencyInjectionImplementation()
    }
}

val Context.di: DependencyInjection
    get() = (this.applicationContext as App).di