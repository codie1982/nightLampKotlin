package com.grnt.nightlampkotlin.model

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.Callback
import kotlin.random.Random

class LambRepository(private val services: LambServices) {
    @Throws(Exception::class)
    fun getInfo():Call<Response>{
        var _info = services.getInfo()
         _info.enqueue(object : Callback<Response>{
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
            }
            override fun onFailure(call: Call<Response>, t: Throwable) {

            }
        })
        return _info
    }
}