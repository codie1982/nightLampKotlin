package com.grnt.nightlampkotlin.di

import com.grnt.nightlampkotlin.model.LambRepository
import com.grnt.nightlampkotlin.model.LambServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

interface DependencyInjection {
    val BASEURL:String
    val repository: LambRepository
    fun setBaseURL(BASEURL: String)
    fun changeBaseURL(BASEURL: String)
}

class DependencyInjectionImplementation() : DependencyInjection {
    override var BASEURL:String = "http://192.168.4.1"
    override lateinit var repository: LambRepository
    init {
        setupRetrofit()
    }
    override fun setBaseURL(BASEURL: String) {
        this.BASEURL = BASEURL
    }
    override fun changeBaseURL(BASEURL: String) {
        this.BASEURL = BASEURL
        setupRetrofit()
    }

    private fun setupRetrofit(){

            val moshi: Moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


            val client: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(this.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            val services =  retrofit.create(LambServices::class.java)

            repository = LambRepository(services)

    }

}