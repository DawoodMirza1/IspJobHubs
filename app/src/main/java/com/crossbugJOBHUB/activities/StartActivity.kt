package com.crossbugJOBHUB.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.fragments.SplashScreenFragment

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        splashFrag()
    }

    private fun splashFrag() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SplashScreenFragment())
            .commit()
    }

}
