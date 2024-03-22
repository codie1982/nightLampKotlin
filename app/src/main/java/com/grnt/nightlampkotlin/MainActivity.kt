package com.grnt.nightlampkotlin

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.grnt.nightlampkotlin.model.LambServices
import com.grnt.nightlampkotlin.model.Response
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory


class MainActivity : AppCompatActivity() {
    lateinit var btnLedOpen: Button
    lateinit var btnLedClose: Button
    val BASEURL = "http://192.168.1.82"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLedOpen = findViewById(R.id.btnLedOpen)
        btnLedClose = findViewById(R.id.btnLedClose)

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
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val services = retrofit.create(LambServices::class.java)

        btnLedOpen.setOnClickListener { view ->
            run {

                val nodemCuServices = services.ledOn()

                nodemCuServices.enqueue(object : Callback<Response>{
                    override fun onResponse(
                        call: Call<Response>,
                        response: retrofit2.Response<Response>
                    ) {
                       println("ledOn onResponse " + response.body()?.led)
                    }

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        println("ledOn  onFailure")
                    }

                })
            }
        }
        btnLedClose.setOnClickListener { view ->
            run {
                val call = services.ledOff()
                call.enqueue(object : Callback<Response>{
                    override fun onResponse(
                        call: Call<Response>,
                        response: retrofit2.Response<Response>
                    ) {
                        println("ledOff onResponse " + response.body()?.led)

                    }

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        println("ledOff  onFailure")
                    }
                })
            }
        }
    }
}

