package com.crossbugJOBHUB.commons

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crossbugJOBHUB.fragments.InternetConnectionErrorFragment
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket


const val SERVER_IP = "8.8.8.8"

interface NetworkStatusListener {
    fun isOnline(online: Boolean)
}

//class NetworkStateChangeReceiver : BroadcastReceiver() {
//
//    companion object {
//
//        var connected = false
//
//    }
//
//    private var networkStateListener: NetworkStatusListener? = null
//
//    override fun onReceive(context: Context, intent: Intent) {
//        connected = isConnectedToNetwork(context)
//
//        if(connected) {
//
//            Handler().postDelayed({
//                InternetCheck(object: Consumer {
//                    override fun accept(internet: Boolean) {
//                        connected = internet
//                        if(context is NetworkStatusListener) {
//                            networkStateListener = context
//                            networkStateListener?.isOnline(internet)
//                        }
//                    }
//                })
//            }, 1500)
//
//        } else {
//            if(context is NetworkStatusListener) {
//                networkStateListener = context
//                networkStateListener?.isOnline(false)
//            }
//        }
//
//    }
//}

interface Consumer {
    fun accept(internet: Boolean)
}

internal class InternetCheck private constructor() : AsyncTask<Void, Void, Boolean>() {

    private lateinit var mConsumer: Consumer
    private var consumer: ((internet: Boolean) -> Unit)? = null

    init {
        execute()
    }

    constructor(mConsumer: Consumer): this() {
        this.mConsumer = mConsumer
    }

    constructor(consumer: (internet: Boolean) -> Unit): this() {
        this.consumer = consumer
    }

    override fun doInBackground(vararg voids: Void): Boolean {
        return try {
            val sock = Socket()
            sock.connect(InetSocketAddress(SERVER_IP, 53), 1500)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }

    }

    override fun onPostExecute(internet: Boolean) {
        if(consumer != null) {
            consumer?.invoke(internet)
        } else {
            mConsumer.accept(internet)
        }
    }
}

fun isConnectedToNetwork(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = cm.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun AppCompatActivity.noInternetFragment() {
    val frag = this.supportFragmentManager.findFragmentByTag(InternetConnectionErrorFragment.TAG)
    if (frag != null) {
        supportFragmentManager.beginTransaction().show(frag).commit()
    } else {
        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, InternetConnectionErrorFragment(), InternetConnectionErrorFragment.TAG)
            .commit()
    }
}

fun Fragment.noInternetFragment() {
    val frag = activity!!.supportFragmentManager.findFragmentByTag(InternetConnectionErrorFragment.TAG)
    if (frag != null) {
        activity!!.supportFragmentManager.beginTransaction().show(frag).commit()
    } else {
        activity!!.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, InternetConnectionErrorFragment(), InternetConnectionErrorFragment.TAG)
            .commit()
    }
}