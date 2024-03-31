package fr.reitag.park_share_front_android.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.reitag.park_share_front_android.R
import fr.reitag.park_share_front_android.ui.log.LogActivity
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timer(false).schedule(1000) {
            val intentLog: Intent = Intent(this@MainActivity, LogActivity::class.java)
            startActivity(intentLog)
            finish()
        }
    }
}