package com.crossbugJOBHUB.fragments


import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.activities.StartActivity


class SplashScreenFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var mActivity: StartActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            mActivity.supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, LoginFragment())
                .commit()
        }, 2000)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (context is StartActivity) {
            mActivity = context
        }
    }
}
