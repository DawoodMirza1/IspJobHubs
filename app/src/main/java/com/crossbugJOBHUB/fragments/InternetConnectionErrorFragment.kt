package com.crossbugJOBHUB.fragments


import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.Consumer
import com.crossbugJOBHUB.commons.InternetCheck
import kotlinx.android.synthetic.main.fragment_internet_connection_error.*

class InternetConnectionErrorFragment : Fragment() {

    companion object {
        @JvmStatic
        val TAG = "InternetConnectionErrorFragment"
    }

    private lateinit var mContext: Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_internet_connection_error, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
//            hideKeyboard(mContext, no_connection)
        }, 600)

        retry_btn.setOnClickListener {

            retry_btn.visibility = View.GONE
            connection_progress.visibility = View.VISIBLE

            InternetCheck(object: Consumer {
                override fun accept(internet: Boolean) {

                    if(internet) {
                        fragmentManager?.beginTransaction()?.remove(this@InternetConnectionErrorFragment)?.commit()
                    } else {
                        connection_progress.visibility = View.GONE
                        retry_btn.visibility = View.VISIBLE
                    }

                }
            })

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

}

