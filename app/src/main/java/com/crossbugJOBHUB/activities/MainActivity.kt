package com.crossbugJOBHUB.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import kotlinx.android.synthetic.main.activity_jobs.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        card1.setOnClickListener {
            startActivity(Intent(this@MainActivity, JobsActivity::class.java))
        }

        card2.setOnClickListener {
            startActivity(Intent(this@MainActivity, JobsActivity::class.java))
        }

        card3.setOnClickListener {
            startActivity(Intent(this@MainActivity, JobsActivity::class.java))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.profile -> {
                startActivity(Intent(this@MainActivity, UserProfileActivity::class.java))
                true
            }
            R.id.logout -> {
                Prefs(this@MainActivity).clear()
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}
