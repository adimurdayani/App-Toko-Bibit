package com.uci.mybibit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
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

class AllProdukActivity : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView
    lateinit var search: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_produk)
        setinit()
        setDisplay()
        setButton()
    }

    private fun setButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
    }

    private var listProduk: ArrayList<ProdukAll> = ArrayList()
    private fun getProduk() {
        sw_data.isRefreshing = true
        ApiConfig.instanceRetrofit.produk().enqueue(object : Callback<ResponsModel> {
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
    }

    private fun setinit() {
        btn_kembali = findViewById(R.id.btn_kembali)
        sw_data = findViewById(R.id.sw_data)
        rc_data = findViewById(R.id.rc_data)
        search = findViewById(R.id.search)
    }

    override fun onResume() {
        getProduk()
        super.onResume()
    }
}