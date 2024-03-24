package com.grnt.nightlampkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager

class WifiBroadcastRecevier(var manager:WifiP2pManager,
                            var mChanel:WifiP2pManager.Channel,
                            var mActivity:MainActivity) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action;
        if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){

        }else if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){

        }else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){

        }else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){

        }

    }

}