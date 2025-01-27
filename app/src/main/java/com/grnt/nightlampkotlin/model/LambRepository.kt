package com.grnt.nightlampkotlin.model

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.Callback
import kotlin.random.Random

class LambRepository(private val services: LambServices) {
    @Throws(Exception::class)
    fun getInfo():Call<Response>{
        return services.checkInfo()
    }
}