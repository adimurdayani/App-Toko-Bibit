package com.uci.mybibit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.uci.mybibit.R
import com.uci.mybibit.adapter.AdapterAllProduk
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.model.ProdukAll
import com.uci.mybibit.model.ResponsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProdukKategoriActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView
    lateinit var search: SearchView
    lateinit var nama: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_produk_kategori)
        setinit()
        setDisplay()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        sw_data.setOnRefreshListener {
            getProduk()
        }
    }

    private var listProduk: ArrayList<ProdukAll> = ArrayList()
    private fun getProduk() {
        sw_data.isRefreshing = true
        val user_id = intent.getIntExtra("user_id", 0)
        ApiConfig.instanceRetrofit.produk_user(user_id).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(call: Call<ResponsModel>, response: Response<ResponsModel>) {
                sw_data.isRefreshing = false
                val res = response.body()
                if (res!!.success == 1) {
                    listProduk = res.produk
                    setDisplay()
                } else {
                    error(response.message())
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data.isRefreshing = false
                error(t.message.toString())
            }
        })
    }

    private fun setDisplay() {
        val layoutManager = GridLayoutManager(this, 2)
        rc_data.adapter = AdapterAllProduk(this, listProduk)
        rc_data.layoutManager = layoutManager

        val adapter = AdapterAllProduk(this, listProduk)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getSearchData().filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        val nama_toko = intent.getStringExtra("nama_toko")
        nama.text = nama_toko
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        sw_data = findViewById(R.id.sw_data)
        rc_data = findViewById(R.id.rc_data)
        search = findViewById(R.id.search)
        nama = findViewById(R.id.nama)
    }

    override fun onResume() {
        getProduk()
        super.onResume()
    }
}