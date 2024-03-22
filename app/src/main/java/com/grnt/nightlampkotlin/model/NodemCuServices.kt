package com.grnt.nightlampkotlin.model

import retrofit2.Call
import retrofit2.http.GET

interface NodemCuServices {
    @GET("/todos/1")
    open fun getInfo(): Call<Response>

    @GET("/ledon")
    open fun ledOn(): Call<Response>

    @GET("/ledoff")
    open fun ledOff(): Call<Response>
}