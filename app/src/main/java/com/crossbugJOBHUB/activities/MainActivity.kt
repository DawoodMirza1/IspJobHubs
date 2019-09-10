package com.crossbugJOBHUB.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.arbazmateen.prefs.Prefs
import com.crossbugJOBHUB.R
import com.crossbugJOBHUB.commons.Keys
import com.crossbugJOBHUB.commons.show
import kotlinx.android.synthetic.main.activity_jobs.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private val status by lazy {
        Prefs(this@MainActivity).getString(Keys.STATUS, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (status == "ADMIN") {
            card_add.show()

            card_add.setOnClickListener {
                startActivity(Intent(this@MainActivity, JobsActivity::class.java))
            }
        }

        card1.setOnClickListener {
            val intent = Intent(this@MainActivity, JobsActivity::class.java)
            intent.putExtra(JobsActivity.category, JobsActivity.CAT_ONE)
            startActivity(intent)
        }

        card2.setOnClickListener {
            val intent = Intent(this@MainActivity, JobsActivity::class.java)
            intent.putExtra(JobsActivity.category, JobsActivity.CAT_TWO)
            startActivity(intent)
        }

        card3.setOnClickListener {
            val intent = Intent(this@MainActivity, JobsActivity::class.java)
            intent.putExtra(JobsActivity.category, JobsActivity.CAT_THREE)
            startActivity(intent)
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
