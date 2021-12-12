package com.uci.mybibit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.uci.mybibit.R
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.ResponsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var btn_login: CardView
    lateinit var btn_register: TextView
    lateinit var btn_kembali: ImageView
    lateinit var e_email: TextInputEditText
    lateinit var e_password: TextInputEditText
    lateinit var l_password: TextInputLayout
    lateinit var l_email: TextInputLayout
    lateinit var email:String
    lateinit var password:String
    lateinit var s:SharedPref
    lateinit var fcmString:String
    lateinit var progress :ProgressBar
    lateinit var txt_login :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        s = SharedPref(this)
        getFCM()
        setButton()
        cekvalidasi()
    }

    private fun getFCM(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Response", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            fcmString = token.toString()
            Log.d("Response fcm", token.toString())
        })
    }

    private fun setButton() {
        btn_login = findViewById(R.id.btn_login)
        btn_register = findViewById(R.id.btn_register)
        btn_kembali = findViewById(R.id.btn_kembali)
        l_email = findViewById(R.id.l_email)
        l_password = findViewById(R.id.l_password)
        e_email = findViewById(R.id.e_email)
        e_password = findViewById(R.id.e_password)
        progress = findViewById(R.id.progress)
        txt_login = findViewById(R.id.txt_login)

        btn_register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        btn_kembali.setOnClickListener {
            super.onBackPressed()
        }
        btn_login.setOnClickListener {
            if (validasi()) {
                login()
            }
        }
    }

    private fun login() {
        email =  e_email.text.toString()
        password =  e_password.text.toString()

        progress.visibility = View.VISIBLE
        txt_login.visibility = View.GONE
        ApiConfig.instanceRetrofit.login(email, e_password.text.toString(), fcmString)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>
                ) {
                    progress.visibility = View.GONE
                    txt_login.visibility = View.VISIBLE
                    val respon = response.body()!!
                    if (respon.success == 1) {
                        s.setStatusLogin(true)
                        s.setUser(respon.data)
                        val intent  =  Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                        Toast.makeText(
                            this@LoginActivity,
                            "Selamat Datang " + respon.data.email,
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        progress.visibility = View.GONE
                        txt_login.visibility = View.VISIBLE
                        error(respon.message)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    progress.visibility = View.GONE
                    txt_login.visibility = View.VISIBLE
                    error(t.message.toString())
                }
            })
    }

    fun error(pesan: String) {
        AestheticDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Error Login")
            .setMessage(pesan)
            .show()
    }

    private fun cekvalidasi() {
        e_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_email.text.toString().isEmpty()) {
                    l_email.isErrorEnabled = false
                } else if (Patterns.EMAIL_ADDRESS.matcher(e_email.text.toString()).matches()) {
                    l_email.isErrorEnabled = false
                } else if (e_email.text.toString().isNotEmpty()) {
                    l_email.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

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
    }

    private fun validasi(): Boolean {
        if (e_email.text.toString().isEmpty()) {
            l_email.isErrorEnabled = true
            l_email.error = "Kolom email tidak boleh kosong!"
            e_email.requestFocus()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(e_email.text.toString()).matches()) {
            l_email.isErrorEnabled = true
            l_email.error = "Format email salah!. Contoh: gunakan @example.com"
            e_email.requestFocus()
            return false
        }

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
        return true
    }
}