package com.uci.mybibit.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.uci.mybibit.R
import com.uci.mybibit.activity.DetailActivity
import com.uci.mybibit.activity.ProdukKategoriActivity
import com.uci.mybibit.helper.Helper
import com.uci.mybibit.model.Alamat
import com.uci.mybibit.model.DaftarToko
import com.uci.mybibit.model.Produk
import com.uci.mybibit.model.ProdukAll
import com.uci.mybibit.util.Util
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterToko(var data: ArrayList<DaftarToko>, val activity: Activity) :
    RecyclerView.Adapter<AdapterToko.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val nama_toko = view.findViewById<TextView>(R.id.nama_toko)
        val layout = view.findViewById<LinearLayout>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_toko, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {
        val a = data[position]

        holder.nama_toko.text = a.nama_toko
        holder.layout.setOnClickListener {
            val intent = Intent(activity, ProdukKategoriActivity::class.java)
            intent.putExtra("user_id", a.id)
            intent.putExtra("nama_toko", a.nama_toko)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}