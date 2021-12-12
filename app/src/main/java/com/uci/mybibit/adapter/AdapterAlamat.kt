package com.uci.mybibit.adapter

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
import com.uci.mybibit.model.Produk
import com.uci.mybibit.util.Util
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterAlamat(var data: ArrayList<Alamat>, var listener: Listeners) :
    RecyclerView.Adapter<AdapterAlamat.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val tv_nama = view.findViewById<TextView>(R.id.nama)
        val tv_phone = view.findViewById<TextView>(R.id.nohp)
        val tv_alamat = view.findViewById<TextView>(R.id.alamat)
        val layout = view.findViewById<CardView>(R.id.layout)
        val cek_alamat = view.findViewById<CheckBox>(R.id.chek)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alamat, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        holder.cek_alamat.isChecked  = a.isSelected
        holder.tv_nama.text = a.name
        holder.tv_phone.text = a.phone
        holder.tv_alamat.text =
            a.alamat + ", " + a.kota + ", " + a.kecamatan + ", " + a.kodepos + ", (" + a.type + ")"

        holder.cek_alamat.setOnClickListener {
            a.isSelected  = true
            listener.onClicked(a)
        }
        holder.layout.setOnClickListener {
            a.isSelected  = true
            listener.onClicked(a)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onClicked(data: Alamat)
    }
}