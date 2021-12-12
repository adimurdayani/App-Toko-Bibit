package com.uci.mybibit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.uci.mybibit.activity.ProdukTerbaru
import com.uci.mybibit.activity.ProdukTerlaris
import com.uci.mybibit.adapter.AdapterProduk
import com.uci.mybibit.adapter.AdapterSlider
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.Produk
import com.uci.mybibit.model.ResponsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentHome : Fragment() {

    lateinit var vp_slider: ViewPager
    lateinit var rc_data: RecyclerView
    lateinit var rc_data2: RecyclerView
    lateinit var sw_data1: SwipeRefreshLayout
    lateinit var nama: TextView
    lateinit var btn_all: TextView
    lateinit var btn_all_terlaris: TextView
    lateinit var s: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        s = SharedPref(requireActivity())
        setButton()
        return view
    }

    private fun setButton() {
        btn_all.setOnClickListener {
            startActivity(Intent(requireActivity(), ProdukTerbaru::class.java))
        }
        btn_all_terlaris.setOnClickListener {
            startActivity(Intent(requireActivity(), ProdukTerlaris::class.java))
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

        val layoutManager2 = LinearLayoutManager(activity)
        layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

        rc_data.adapter = AdapterProduk(requireActivity(), listProduk)
        rc_data.layoutManager = layoutManager

        rc_data2.adapter = AdapterProduk(requireActivity(), listProduk2)
        rc_data2.layoutManager = layoutManager2
    }

    private var listProduk: ArrayList<Produk> = ArrayList()
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
                    listProduk = res.produk
                    displayProduk()
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
            }
        })
    }

    private var listProduk2: ArrayList<Produk> = ArrayList()
    private fun getProduk2() {
        sw_data1.isRefreshing = true
        ApiConfig.instanceRetrofit.produkId(2).enqueue(object : Callback<ResponsModel> {
            override fun onResponse(call: Call<ResponsModel>, response: Response<ResponsModel>) {
                sw_data1.isRefreshing = false
                val res = response.body()!!
                if (res.success == 1) {
                    listProduk2 = res.produk
                    displayProduk()
                }else{
                    error(response.message())
                }
            }

            override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                sw_data1.isRefreshing = false
                error(t.message.toString())
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
    }

    override fun onResume() {
        getProduk()
        getProduk2()
        super.onResume()
    }
}