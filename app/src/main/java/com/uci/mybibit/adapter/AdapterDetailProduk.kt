package com.uci.mybibit.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.uci.mybibit.model.DetailTransaksi
import com.uci.mybibit.model.Produk
import com.uci.mybibit.model.Transaksi
import com.uci.mybibit.util.Util
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterDetailProduk(var data: ArrayList<DetailTransaksi>) :
    RecyclerView.Adapter<AdapterDetailProduk.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val nama_produk = view.findViewById<TextView>(R.id.nama_produk)
        val berat_barang = view.findViewById<TextView>(R.id.berat_barang)
        val harga_produk = view.findViewById<TextView>(R.id.harga_produk)
        val item = view.findViewById<TextView>(R.id.item)
        val total = view.findViewById<TextView>(R.id.total)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_detail_keranjang, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        val nama_produk = a.produk.name
        val p = a.produk
        holder.nama_produk.text = nama_produk
//        holder.berat_barang.text =
        holder.harga_produk.text = Helper().formatRupiah(p.harga)
        holder.total.text = Helper().formatRupiah(a.total_harga)
        holder.item.text = a.total_item.toString() + " Items"

        val imageUrl = Util.produkUrl + p.image
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.jagung)
            .error(R.drawable.jagung)
            .into(holder.image)

        holder.layout.setOnClickListener {
//            listener.onClicked(a)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onClicked(data: DetailTransaksi)
    }
}