package com.uci.mybibit.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.uci.mybibit.R
import com.uci.mybibit.activity.DetailActivity
import com.uci.mybibit.helper.Helper
import com.uci.mybibit.model.Alamat
import com.uci.mybibit.model.Produk
import com.uci.mybibit.model.Transaksi
import com.uci.mybibit.util.Util
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterRiwayat(var data: ArrayList<Transaksi>, var listener: Listeners) :
    RecyclerView.Adapter<AdapterRiwayat.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val nama = view.findViewById<TextView>(R.id.nama)
        val tanggal = view.findViewById<TextView>(R.id.tanggal)
        val harga = view.findViewById<TextView>(R.id.harga)
        val item = view.findViewById<TextView>(R.id.item)
        val status = view.findViewById<TextView>(R.id.status)
        val detail = view.findViewById<TextView>(R.id.detail)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    lateinit var contex: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        contex = parent.context
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]
        val nama = a.details[0].produk.name

        val formatBaru = "d MMM yyyy"

        holder.nama.text = nama
        holder.tanggal.text = Helper().convertTanggal(a.created_at, formatBaru)
        holder.item.text = a.total_item + " Items"
        holder.harga.text = Helper().formatRupiah(a.total_transfer)
        holder.status.text = a.status

//        set color status
        var color = contex.getColor(R.color.menunggu)
        if (a.status == "SELESAI") color = contex.getColor(R.color.selesai)
        else if (a.status == "BATAL") color = contex.getColor(R.color.batal)
        holder.status.setTextColor(color)

        holder.layout.setOnClickListener {
            listener.onClicked(a)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private var searchData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val searchList: java.util.ArrayList<Transaksi> = java.util.ArrayList<Transaksi>()
            if (constraint.toString().isEmpty()) {
                searchList.addAll(data)
            } else {
                for (getRekamMedik in data) {
                    if (getRekamMedik.details[0].produk.name.toLowerCase(Locale.getDefault())
                            .contains(constraint.toString().toLowerCase(Locale.getDefault()))
                    ) {
                        searchList.add(getRekamMedik)
                    }
                }
            }
            val results = FilterResults()
            results.values = searchList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            data.clear()
            data.addAll(results.values as Collection<Transaksi>)
            notifyDataSetChanged()
        }
    }

    fun getSearchData(): Filter {
        return searchData
    }

    interface Listeners {
        fun onClicked(data: Transaksi)
    }
}