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
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.grnt.nightlampkotlin.di.view.tab_main.AnimatedColorFragment
import com.grnt.nightlampkotlin.di.view.tab_main.ColorFragment
import com.grnt.nightlampkotlin.di.view.tab_main.TabMainActivity
import com.grnt.nightlampkotlin.di.view.tab_main.WeeklyColorFragment
import com.grnt.nightlampkotlin.model.ConnectionResponse
import com.grnt.nightlampkotlin.model.LambRepository
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
import java.nio.channels.SelectableChannel


class MainActivity : BaseActivity() {
    var repository: LambRepository? = null

    private lateinit var wifiManager: WifiManager
    lateinit var btnControl :Button
    private lateinit var wifiScanReceiver:BroadcastReceiver

    private lateinit var mWifip2pManager: WifiP2pManager
    private lateinit var mChannel:WifiP2pManager.Channel
    private lateinit var mBroadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter:IntentFilter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnControl = findViewById(R.id.btnControl)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mWifip2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mWifip2pManager.initialize(this, Looper.getMainLooper(),null)


        mBroadcastReceiver = WifiBroadcastRecevier(mWifip2pManager,mChannel,this)

        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        repository = di.repository
        setOnClickListener()
        //sendMainTabScreen()
    }

    private fun setOnClickListener() {

     btnControl.setOnClickListener(object : OnClickListener{
         override fun onClick(v: View?) {
             var isFail : Boolean = false
          var nums = arrayOf(1,2,3,4)
             for(num in nums){
                 var url = "http://198.162.1.$num"
                 di.changeBaseURL(url)
                 var _info = di.repository.getInfo()
                 _info.enqueue(object : Callback<Response>{
                     override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {

                         println("onResponse isSuccessful -" + response.isSuccessful)
                         println("onResponse errorBody - " + response.errorBody())
                         println("onResponse raw - " + response.raw())
                         println("onResponse headers - " + response.headers())

                     }
                     override fun onFailure(call: Call<Response>, t: Throwable) {
                         println("onFailure : " + call.request().url)
                         println("onFailure : " + call.request().headers)
                         println("onFailure")
                     }
                 })
             }
         }
     })
    }

    private fun sendMainTabScreen() {
        var intent = Intent(this@MainActivity,TabMainActivity::class.java)
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
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



    private fun scanSuccess() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        //wifiManager.scanResults()
    }

}

