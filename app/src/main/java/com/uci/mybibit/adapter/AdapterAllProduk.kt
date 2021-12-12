package com.uci.mybibit.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.uci.mybibit.model.Produk
import com.uci.mybibit.util.Util
import java.util.*

class AdapterAllProduk(var activity: Activity, var data: ArrayList<Produk>) :
    RecyclerView.Adapter<AdapterAllProduk.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val tv_nama = view.findViewById<TextView>(R.id.nama_produk)
        val tv_harga = view.findViewById<TextView>(R.id.harga)
        val tv_gambar = view.findViewById<ImageView>(R.id.image)
        val layout = view.findViewById<CardView>(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_all_produk, parent, false)
        return HolderData(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HolderData, position: Int) {
        holder.tv_nama.text = data[position].name
        holder.tv_harga.text = Helper().formatRupiah(data[position].harga)
            .format(Integer.valueOf(data[position].harga))
        val imageUrl =
            Util.produkUrl + data[position].image
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.jagung)
            .error(R.drawable.jagung)
            .into(holder.tv_gambar)

        holder.layout.setOnClickListener {
            val intent = Intent(activity, DetailActivity::class.java)
            val str = Gson().toJson(data[position], Produk::class.java)
            intent.putExtra("extra", str)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private var searchData: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val searchList: java.util.ArrayList<Produk> = java.util.ArrayList<Produk>()
            if (constraint.toString().isEmpty()) {
                searchList.addAll(data)
            } else {
                for (getRekamMedik in data) {
                    if (getRekamMedik.name.toLowerCase(Locale.ROOT)
                            .contains(constraint.toString().toLowerCase(Locale.ROOT))
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
            data.addAll(results.values as Collection<Produk>)
            notifyDataSetChanged()
        }
    }

    fun getSearchData(): Filter {
        return searchData
    }
}