package com.uci.mybibit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.uci.mybibit.R
import com.uci.mybibit.adapter.AdapterProduk
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.helper.Helper
import com.uci.mybibit.model.Produk
import com.uci.mybibit.model.ProdukAll
import com.uci.mybibit.model.ResponsModel
import com.uci.mybibit.room.MyDatabase
import com.uci.mybibit.util.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var txt_namaproduk: TextView
    private lateinit var txt_hargaproduk: TextView
    private lateinit var namabarang: TextView
    lateinit var txt_deskripsi: TextView
    private lateinit var image: ImageView
    private lateinit var btn_kembali: ImageView
    lateinit var btn_keranjang: ImageView
    lateinit var btn_notifikasi: ImageView
    private lateinit var btn_beli: LinearLayout
    lateinit var produk: Produk
    lateinit var produk2: ProdukAll
    lateinit var myDb: MyDatabase
    lateinit var div_angka: RelativeLayout
    lateinit var tv_angka: TextView
    lateinit var berat: TextView
    lateinit var stok: TextView
    lateinit var email: TextView
    lateinit var rc_data: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setIni()
        myDb = MyDatabase.getInstance(this)!!
        getDetail()
        displayProduk()
        ceckkeranjang()
    }

    private fun getDetail() {
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data, Produk::class.java)
        produk2 = Gson().fromJson<ProdukAll>(data, ProdukAll::class.java)

        txt_namaproduk.text = produk.name
        txt_hargaproduk.text = Helper().formatRupiah(produk.harga)
        txt_deskripsi.text = produk.deskripsi
        namabarang.text = produk.name
        berat.text = produk.berat
        stok.text = produk.stok
        email.text = produk2.user.email
        Picasso.get()
            .load(Util.produkUrl + produk.image)
            .placeholder(R.drawable.jagung)
            .error(R.drawable.jagung)
            .into(image)

        btn_keranjang.setOnClickListener {
            val produkData = myDb.daoKeranjang().getProduk(produk.id)
            if (produkData == null) {
                insert()
            } else {
                produkData.jumlah = produkData.jumlah + 1
                update(produkData)
            }
        }
        btn_kembali.setOnClickListener {
            super.onBackPressed()
        }

        btn_notifikasi.setOnClickListener {
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }

        btn_beli.setOnClickListener {
            val produkData = myDb.daoKeranjang().getProduk(produk.id)
            if (produkData == null) {
                insert()
            } else {
                produkData.jumlah = produkData.jumlah + 1
                update(produkData)
            }
            val intent = Intent("event:keranjang")
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            onBackPressed()
        }
    }

    private fun displayProduk() {
        val layoutManager = GridLayoutManager(this, 2)

        rc_data.adapter = AdapterProduk(this, listProduk)
        rc_data.layoutManager = layoutManager
    }

    private var listProduk: ArrayList<ProdukAll> = ArrayList()
    private fun getProduk() {
        val data = intent.getStringExtra("extra")
        produk = Gson().fromJson<Produk>(data, Produk::class.java)
        ApiConfig.instanceRetrofit.produkId(produk.kategori_id)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    val res = response.body()!!
                    if (res.success == 1) {
                        listProduk = res.produk_id
                        displayProduk()
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                }
            })
    }

    fun insert() {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().insert(produk) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ceckkeranjang()
                Log.d("respons", "data inserted")
            })

    }

    fun update(produkData: Produk) {
        CompositeDisposable().add(Observable.fromCallable { myDb.daoKeranjang().update(produkData) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ceckkeranjang()
                Log.d("respons", "data inserted")
            })

    }

    private fun ceckkeranjang() {
        var dataKeranjang = myDb.daoKeranjang().getAll()
        if (dataKeranjang.isNotEmpty()) {
            div_angka.visibility = View.VISIBLE
            tv_angka.text = "" + dataKeranjang.size
        } else {
            div_angka.visibility = View.GONE
        }
    }

    private fun setIni() {
        txt_namaproduk = findViewById(R.id.nama_p)
        txt_hargaproduk = findViewById(R.id.harga_produk)
        image = findViewById(R.id.image_produk)
        txt_deskripsi = findViewById(R.id.deskripsi)
        btn_keranjang = findViewById(R.id.btn_keranjang)
        namabarang = findViewById(R.id.nama)
        btn_kembali = findViewById(R.id.btn_kembali)
        btn_notifikasi = findViewById(R.id.btn_notifikasi)
        div_angka = findViewById(R.id.div_angka)
        tv_angka = findViewById(R.id.tv_angka)
        btn_beli = findViewById(R.id.btn_beli1)
        rc_data = findViewById(R.id.rc_data)
        berat = findViewById(R.id.berat)
        stok = findViewById(R.id.stok)
        email = findViewById(R.id.email)
    }

    override fun onResume() {
        getProduk()
        super.onResume()
    }
}