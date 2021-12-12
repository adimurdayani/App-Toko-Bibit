package com.uci.mybibit.activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.thecode.aestheticdialogs.*
import com.uci.mybibit.R
import com.uci.mybibit.adapter.AdapterBank
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.model.Bank
import com.uci.mybibit.model.Checkout
import com.uci.mybibit.model.ResponsModel
import com.uci.mybibit.model.Transaksi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PembayaranActivity : AppCompatActivity() {

    lateinit var rc_data: RecyclerView
    lateinit var btn_kembali: ImageView
    lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)
        rc_data = findViewById(R.id.rc_data)
        btn_kembali = findViewById(R.id.btn_kembali)
        dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        displayBank()
    }

    fun displayBank() {
        val arrarBank = ArrayList<Bank>()
        arrarBank.add(Bank("Bank BCA", "091271231010", "Adi Murdayani", R.drawable.bca))
        arrarBank.add(Bank("Bank BRI", "019271231289", "Dewi Astuti", R.drawable.bri))
        arrarBank.add(Bank("Bank Mandiri", "090912837110", "Murdayani", R.drawable.mandiri))

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rc_data.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rc_data.layoutManager = layoutManager
        rc_data.adapter = AdapterBank(arrarBank, object : AdapterBank.Listeners {
            override fun onCreate(data: Bank, index: Int) {
                bayar(data)
            }

        })
    }

    fun bayar(bank: Bank) {
        val json = intent.getStringExtra("extra")!!.toString()
        val checkout = Gson().fromJson(json, Checkout::class.java)
        checkout.bank = bank.nama

        dialog.setCancelable(true)
        dialog.setMessage("Loading...")
        ApiConfig.instanceRetrofit.checkout(checkout)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    dialog.setCancelable(false)
                    val res = response.body()!!
                    if (res.success == 1) {
                        val jsBank = Gson().toJson(bank, Bank::class.java)
                        val jsTransaksi = Gson().toJson(res.transaksi, Transaksi::class.java)
                        val jsCheckout = Gson().toJson(checkout, Checkout::class.java)
                        Log.d("Respon: ", "Data Bank: " + jsBank + "Data Transaksi: " + jsTransaksi)

                        AestheticDialog.Builder(this@PembayaranActivity, DialogStyle.FLAT, DialogType.SUCCESS)
                            .setTitle("Metode Pembayaran Sukses")
                            .setMessage("Anda telah berhasil memilih metode pembayaran, klik tombol untuk melihat detail.")
                            .setCancelable(false)
                            .setDarkMode(false)
                            .setGravity(Gravity.CENTER)
                            .setAnimation(DialogAnimation.SLIDE_DOWN)
                            .setOnClickListener(object : OnDialogClickListener {
                                override fun onClick(dialog: AestheticDialog.Builder) {
                                    val intent = Intent(this@PembayaranActivity, SuksesActivity::class.java)
                                    intent.putExtra("bank", jsBank)
                                    intent.putExtra("transaksi", jsTransaksi)
                                    intent.putExtra("checkout", jsCheckout)
                                    startActivity(intent)
                                    finish()
                                    dialog.dismiss()
                                }
                            })
                            .show()
                    } else {
                        error(res.message)
                        dialog.setCancelable(false)
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    Log.d("Message", "Error: " + t.stackTraceToString())
                    error(t.message.toString())
                    dialog.setCancelable(false)
                }
            })
    }

    fun error(pesan: String) {
        AestheticDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Error Koneksi")
            .setMessage(pesan)
            .show()
    }
}