package com.uci.mybibit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.Gravity
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.thecode.aestheticdialogs.*
import com.uci.mybibit.R

class UbahPassword : AppCompatActivity() {
   lateinit var btn_kembali:ImageView
   lateinit var l_password:TextInputLayout
   lateinit var e_password:TextInputEditText
   lateinit var l_konf_password:TextInputLayout
   lateinit var e_konf_password:TextInputEditText
   lateinit var btn_simpan:CardView
   lateinit var progress:ProgressBar
   lateinit var txt_register:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah_password)
        setInit()
        setButton()
        cekvalidasi()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()){

            }
        }
    }

    private fun setInit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        l_password = findViewById(R.id.l_password)
        e_password = findViewById(R.id.e_password)
        l_konf_password = findViewById(R.id.l_konf_password)
        e_konf_password = findViewById(R.id.e_konf_password)
        btn_simpan = findViewById(R.id.btn_simpan)
        progress = findViewById(R.id.progress)
        txt_register = findViewById(R.id.txt_register)
    }

    private fun cekvalidasi() {

        e_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_password.text.toString().isEmpty()) {
                    l_password.isErrorEnabled = false
                } else if (e_password.text.toString().length > 7) {
                    l_password.isErrorEnabled = false
                } else if (e_password.text.toString().isNotEmpty()) {
                    l_password.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        e_konf_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_konf_password.text.toString().isEmpty()) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString().length > 7) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString()
                        .matches(e_password.text.toString().toRegex())
                ) {
                    l_konf_password.isErrorEnabled = false
                } else if (e_konf_password.text.toString().isNotEmpty()) {
                    l_konf_password.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validasi(): Boolean {

        if (e_password.text.toString().isEmpty()) {
            l_password.isErrorEnabled = true
            l_password.error = "Kolom password tidak boleh kosong!"
            e_password.requestFocus()
            return false
        } else if (e_password.text.toString().length < 6) {
            l_password.isErrorEnabled = true
            l_password.error = "Password tidak boleh kurang dari 6 karakter!"
            e_password.requestFocus()
            return false
        }
        if (e_konf_password.text.toString().isEmpty()) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Kolom konfirmasi password tidak boleh kosong!"
            e_konf_password.requestFocus()
            return false
        } else if (e_konf_password.text.toString().length < 6) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Konfirmasi password tidak boleh kurang dari 6 karakter!"
            e_konf_password.requestFocus()
            return false
        } else if (!e_konf_password.text.toString().matches(e_password.text.toString().toRegex())) {
            l_konf_password.isErrorEnabled = true
            l_konf_password.error = "Konfirmasi password tidak sama dengan password!"
            e_konf_password.requestFocus()
            return false
        }
        return true
    }

    fun sukses(pesan: String){
        AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
            .setTitle("Sukses")
            .setMessage(pesan)
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SLIDE_DOWN)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            })
            .show()
    }

    fun error(pesan: String) {
        AestheticDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Error Koneksi")
            .setMessage(pesan)
            .show()
    }
}