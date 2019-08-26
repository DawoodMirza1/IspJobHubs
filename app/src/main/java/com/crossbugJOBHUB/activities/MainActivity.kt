package com.crossbugJOBHUB.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.crossbugJOBHUB.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        card1.setOnClickListener {
            startActivity(Intent(this@MainActivity, JobsActivity::class.java))
        }

        card1.setOnClickListener {
            startActivity(Intent(this@MainActivity, JobsActivity::class.java))
        }

        card1.setOnClickListener {
            startActivity(Intent(this@MainActivity, JobsActivity::class.java))
        }

    }
}
