package com.uci.mybibit.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.uci.mybibit.R
import com.uci.mybibit.activity.AllProdukActivity
import com.uci.mybibit.activity.ProdukTerbaru
import com.uci.mybibit.activity.ProdukTerlaris
import com.uci.mybibit.adapter.AdapterProduk
import com.uci.mybibit.adapter.AdapterSlider
import com.uci.mybibit.adapter.AdapterToko
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.DaftarToko
import com.uci.mybibit.model.Produk
import com.uci.mybibit.model.ProdukAll
import com.uci.mybibit.model.ResponsModel
import com.uci.mybibit.room.MyDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentHome : Fragment() {

    lateinit var vp_slider: ViewPager
    lateinit var rc_data: RecyclerView
    lateinit var rc_data2: RecyclerView
    lateinit var rc_data_toko: RecyclerView
    lateinit var sw_data1: SwipeRefreshLayout
    lateinit var nama: TextView
    lateinit var btn_all: TextView
    lateinit var search: ImageView
    lateinit var btn_all_terlaris: TextView
    lateinit var btn_notifikasi: ImageView
    lateinit var div_angka: RelativeLayout
    lateinit var tv_angka: TextView
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireContext())!!
        s = SharedPref(requireActivity())
        setButton()
        return view
    }

    private fun ceckkeranjang() {
        val dataKeranjang = myDb.daoKeranjang().getAll()
        if (dataKeranjang.isNotEmpty()) {
            div_angka.visibility = View.VISIBLE
            tv_angka.text = "" + dataKeranjang.size
        } else {
            div_angka.visibility = View.GONE
        }
    }

    private fun setButton() {
        btn_all.setOnClickListener {
            startActivity(Intent(requireActivity(), ProdukTerbaru::class.java))
        }
        btn_all_terlaris.setOnClickListener {
            startActivity(Intent(requireActivity(), ProdukTerlaris::class.java))
        }

        search.setOnClickListener {
            startActivity(Intent(requireContext(), AllProdukActivity::class.java))
        }
    }

    fun displayProduk() {
        if (s.getUser()?.name != null) {
            nama.visibility = View.VISIBLE
            nama.text = s.getUser()?.name
        } else {
            nama.visibility = View.GONE
        }
        val arrSlider = ArrayList<Int>()
        arrSlider.add(R.drawable.slide1)
        arrSlider.add(R.drawable.slide1)
        arrSlider.add(R.drawable.slide2)

        val adapterSlider = AdapterSlider(arrSlider, activity)
        vp_slider.adapter = adapterSlider

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rc_data.adapter = AdapterProduk(requireActivity(), listProduk)
        rc_data.layoutManager = layoutManager

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL
        rc_data2.adapter = AdapterProduk(requireActivity(), listProduk2)
        rc_data2.layoutManager = layoutManager2

        val layoutManager3 = LinearLayoutManager(activity)
        layoutManager3.orientation = LinearLayoutManager.HORIZONTAL
        rc_data_toko.adapter = AdapterToko(listtoko, requireActivity())
        rc_data_toko.layoutManager = layoutManager3

    }

    private var listtoko: ArrayList<DaftarToko> = ArrayList()
    private fun getToko() {
        ApiConfig.instanceRetrofit.daftar_toko().enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                val res = response.body()!!
                if (res.success == 1) {
                    listtoko = res.daftar_toko
                    displayProduk()
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private var listProduk: ArrayList<ProdukAll> = ArrayList()
    private fun getProduk() {
        sw_data1.isRefreshing = true
        ApiConfig.instanceRetrofit.produkId(1).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(
                call: Call<ResponsModel>,
                response: Response<ResponsModel>,
            ) {
                sw_data1.isRefreshing = false
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk = res.produk_id
                    displayProduk()
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    private var listProduk2: ArrayList<ProdukAll> = ArrayList()
    private fun getProduk2() {
        sw_data1.isRefreshing = true
        ApiConfig.instanceRetrofit.produkId(2).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(call: Call<ResponsModel>, response: Response<ResponsModel>) {
                sw_data1.isRefreshing = false
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk2 = res.produk_id
                    displayProduk()
                } else {
                    error(response.message())
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                Log.d("Response", "Error: " + t.message)
            }
        })
    }

    fun error(pesan: String) {
        AestheticDialog.Builder(requireActivity(), DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Error")
            .setMessage(pesan)
            .show()
    }

    private fun init(view: View) {
        vp_slider = view.findViewById(R.id.vp_slider)
        rc_data = view.findViewById(R.id.rc_data)
        rc_data2 = view.findViewById(R.id.rc_data2)
        sw_data1 = view.findViewById(R.id.sw_data1)
        nama = view.findViewById(R.id.nama)
        btn_all = view.findViewById(R.id.btn_all)
        btn_all_terlaris = view.findViewById(R.id.btn_all_terlaris)
        rc_data_toko = view.findViewById(R.id.rc_data_toko)
        search = view.findViewById(R.id.search)
        btn_notifikasi = view.findViewById(R.id.btn_notifikasi)
        div_angka = view.findViewById(R.id.div_angka)
        tv_angka = view.findViewById(R.id.tv_angka)
    }

    override fun onResume() {
        getProduk()
        getProduk2()
        getToko()
        ceckkeranjang()
        super.onResume()
    }
}