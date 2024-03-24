package com.grnt.nightlampkotlin

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.WifiSsid
import android.net.wifi.hotspot2.PasspointConfiguration
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.PatternMatcher
import android.provider.Settings.ACTION_WIFI_ADD_NETWORKS
import android.provider.Settings.ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED
import android.provider.Settings.ADD_WIFI_RESULT_ALREADY_EXISTS
import android.provider.Settings.ADD_WIFI_RESULT_SUCCESS
import android.provider.Settings.EXTRA_WIFI_NETWORK_LIST
import android.provider.Settings.EXTRA_WIFI_NETWORK_RESULT_LIST
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.grnt.nightlampkotlin.model.ConnectionResponse
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


class MainActivity : BaseActivity() {
    private lateinit var wifiManager: WifiManager
    private lateinit var services: LambServices
    lateinit var btnLedOpen: Button
    lateinit var btnLedClose: Button
    private lateinit var wifiScanReceiver:BroadcastReceiver
    val BASEURL = "http://192.168.1.82"

    private lateinit var mWifip2pManager: WifiP2pManager
    private lateinit var mChannel:WifiP2pManager.Channel
    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter:IntentFilter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLedOpen = findViewById(R.id.btnLedOpen)
        btnLedClose = findViewById(R.id.btnLedClose)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifip2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mWifip2pManager.initialize(this, Looper.getMainLooper(),null)

        //requestMyNetwork();
        mBroadcastReceiver = WifiBroadcastRecevier(mWifip2pManager,mChannel,this)

        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)


        if(!wifiManager.isWifiEnabled){

        }else{
            wifiScanReceiver = object : BroadcastReceiver(){
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onReceive(p0: Context?, p1: Intent?) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    if (success) {
                        scanSuccess()
                    } else {
                        scanFailure()
                    }
                }
            }
        }


        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)


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

    override fun onResume() {
        super.onResume()
        registerReceiver(mBroadcastReceiver,intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mBroadcastReceiver)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

       var results:List<ScanResult> =  wifiManager.scanResults

        for(result :ScanResult in results ){
         if(result.wifiSsid.toString() == "\"NightLamp\""){
             var findWifiSSID : WifiSsid = result.wifiSsid!!
             val suggestion1 = WifiNetworkSuggestion.Builder()
                 .setWifiSsid(findWifiSSID)
                 .setWpa2Passphrase("12345678")
                 .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                 .build();

             val suggestion2 = WifiNetworkSuggestion.Builder()
                 .setSsid("NightLamp")
                 .setWpa2Passphrase("12345678")
                 .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                 .build();

             val suggestion3 = WifiNetworkSuggestion.Builder()
                 .setSsid("NightLamp")
                 .setWpa3Passphrase("12345678")
                 .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                 .build();


             val suggestionsList = listOf(suggestion1,suggestion2,suggestion3);
             val status = wifiManager.addNetworkSuggestions(suggestionsList);
             if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                 // do error handling here
             }else{


                 val suggestions = ArrayList<WifiNetworkSuggestion>()

                 // WPA2 configuration
                 suggestions.add(
                     WifiNetworkSuggestion.Builder()
                         .setSsid("NightLamp")
                         .setWpa2Passphrase("12345678")
                         .build()
                 )

                 // Open configuration
                 suggestions.add(
                     WifiNetworkSuggestion.Builder()
                         .setSsid("NightLamp")
                         .build()
                 )

                 // Create intent
                 val bundle = Bundle()
                 bundle.putParcelableArrayList(EXTRA_WIFI_NETWORK_LIST, suggestions)
                 val intent = Intent(ACTION_WIFI_ADD_NETWORKS)
                 intent.putExtras(bundle)

                 // Launch intent
                 startActivityForResult(intent, 0)

                 /*setupRetrofit("http://192.168.4.1");
                 var connectionCall = services.setConnection()
                 connectionCall.enqueue(object : Callback<ConnectionResponse>{
                     override fun onResponse(
                         call: Call<ConnectionResponse>,
                         response: retrofit2.Response<ConnectionResponse>
                     ) {
                            println("Baglanti : " + response.body()?.isConnect)
                            println("IP No : " + response.body()?.ip)
                     }
                     override fun onFailure(call: Call<ConnectionResponse>, t: Throwable) {
                            println("Baglanti Basarisiz")
                     }
                 })*/
             }

            // Optional (Wait for post connection broadcast to one of your suggestions)
             val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

             val broadcastReceiver = object : BroadcastReceiver() {
                 override fun onReceive(context: Context, intent: Intent) {
                     if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                         return;
                     }
                     // do post connect processing here
                 }
             };
         }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            // user agreed to save configurations: still need to check individual results
            if (data != null && data.hasExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                for (code in data.getIntegerArrayListExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)!!) {
                    when (code) {
                        ADD_WIFI_RESULT_SUCCESS ->
                            println("ADD_WIFI_RESULT_SUCCESS - Configuration saved or modified") // Configuration saved or modified
                            ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED ->
                                println("ADD_WIFI_RESULT_ADD_OR_UPDATE_FAILED - Something went wrong - invalid configuration") // Something went wrong - invalid configuration
                            ADD_WIFI_RESULT_ALREADY_EXISTS ->{

                                println("ADD_WIFI_RESULT_ALREADY_EXISTS - Something went wrong - invalid configuration")
                                requestMyNetwork()
                            }

                        else ->
                            println("Else")
                    }
                }
            }
        } else {
            // User refused to save configurations
        }
    }

    private fun requestMyNetwork() {
        setupRetrofit("http://192.168.4.1");
             var connectionCall = services.setConnection()
             connectionCall.enqueue(object : Callback<ConnectionResponse>{
                 override fun onResponse(
                     call: Call<ConnectionResponse>,
                     response: retrofit2.Response<ConnectionResponse>
                 ) {
                        println("Baglanti : " + response.body()?.isConnect)
                        println("IP No : " + response.body()?.ip)
                 }
                 override fun onFailure(call: Call<ConnectionResponse>, t: Throwable) {
                        println("Baglanti Basarisiz")
                 }
             })
    }


    private fun scanSuccess() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        //wifiManager.scanResults()
    }

    private fun setupRetrofit(baseUrl:String) {
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
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        services =  retrofit.create(LambServices::class.java)
    }
}

