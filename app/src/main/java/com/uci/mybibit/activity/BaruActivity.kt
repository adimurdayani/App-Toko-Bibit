package com.uci.mybibit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import com.uci.mybibit.R
import com.uci.mybibit.helper.SharedPref

class BaruActivity : AppCompatActivity() {
    lateinit var btn_login: LinearLayout
    lateinit var btn_register: LinearLayout
    lateinit var btn_kembali: ImageView
//    lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_baru)
//        s = SharedPref(this)
        setButton()
    }

    private fun setButton() {
        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_register)
        btn_kembali = findViewById(R.id.btn_kembali)

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btn_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btn_kembali.setOnClickListener {
            super.onBackPressed()
        }
    }
}