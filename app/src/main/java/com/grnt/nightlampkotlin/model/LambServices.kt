package com.grnt.nightlampkotlin.model

import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET

interface LambServices {
    @GET("/")
    open fun getInfo(): Call<Response>

    @GET("/connection")
    open fun setConnection(): Call<ConnectionResponse>

    @GET("/ledon")
    open fun ledOn(): Call<Response>

    @GET("/ledoff")
    open fun ledOff(): Call<Response>
}