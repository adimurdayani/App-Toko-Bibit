package com.uci.mybibit.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.uci.mybibit.R

class MainActivity : AppCompatActivity() {
    val handler: Handler  = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler.postDelayed(Runnable {
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }, 2000)
    }
}