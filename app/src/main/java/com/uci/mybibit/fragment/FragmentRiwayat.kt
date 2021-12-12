package com.uci.mybibit.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.uci.mybibit.R
import com.uci.mybibit.activity.DetailRiwayatActivity
import com.uci.mybibit.adapter.AdapterRiwayat
import com.uci.mybibit.api.ApiConfig
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.ResponsModel
import com.uci.mybibit.model.Transaksi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentRiwayat : Fragment() {

    lateinit var search: SearchView
    lateinit var total_list: TextView
    lateinit var sw_data: SwipeRefreshLayout
    lateinit var rc_data: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_riwayat, container, false)
        init(view)
        getRiwayat()
        return view
    }

    private fun getRiwayat() {
        val id = SharedPref(requireActivity()).getUser()?.id
        sw_data.isRefreshing = true
        if (id != null) {
            ApiConfig.instanceRetrofit.getRiwayat(id).enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    sw_data.isRefreshing = false
                    val res = response.body()
                    if (res!!.success == 1) {
                        displayRiwayat(res.transaksis)
                    } else {
                        Log.d("Respon", "Error: " + res.message)
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    sw_data.isRefreshing = false
                    Log.d("Respon", "Error: " + t.message)
                    error(t.message.toString())
                }
            })
        }
    }

    fun displayRiwayat(array: ArrayList<Transaksi>) {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        total_list.text = array.size.toString()

        rc_data.adapter = AdapterRiwayat(array, object : AdapterRiwayat.Listeners {
            override fun onClicked(data: Transaksi) {
                val json = Gson().toJson(data, Transaksi::class.java)
                val inten = Intent(requireActivity(), DetailRiwayatActivity::class.java)
                inten.putExtra("transaksi", json)
                startActivity(inten)
            }

        })
        rc_data.layoutManager = layoutManager

        sw_data.setOnRefreshListener {
            getRiwayat()
        }

        val adapter = AdapterRiwayat(array, object : AdapterRiwayat.Listeners {
            override fun onClicked(data: Transaksi) {

            }
        })
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.getSearchData().filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getRiwayat()
                return false
            }
        })
    }

    private fun init(view: View) {
        search = view.findViewById(R.id.search)
        total_list = view.findViewById(R.id.total_list)
        sw_data = view.findViewById(R.id.sw_data)
        rc_data = view.findViewById(R.id.rc_data)
    }


    fun error(pesan: String) {
        AestheticDialog.Builder(requireActivity(), DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Title")
            .setMessage(pesan)
            .show()
    }

    override fun onResume() {
        getRiwayat()
        super.onResume()
    }
}