package com.uci.mybibit.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.uci.mybibit.R
import com.uci.mybibit.helper.Helper
import com.uci.mybibit.model.Produk
import com.uci.mybibit.room.MyDatabase
import com.uci.mybibit.util.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class AdapterKeranjang(
    var activity: Activity,
    var data: ArrayList<Produk>,
    var listener: Listeners
) :
    RecyclerView.Adapter<AdapterKeranjang.HolderData>() {
    class HolderData(view: View) : RecyclerView.ViewHolder(view) {
        val tv_nama = view.findViewById<TextView>(R.id.nama_produk)
        val tv_harga = view.findViewById<TextView>(R.id.total)
        val tv_gambar = view.findViewById<ImageView>(R.id.image)
        val layout = view.findViewById<CardView>(R.id.layout)
        val btn_tambah = view.findViewById<ImageView>(R.id.btn_add)
        val btn_kurang = view.findViewById<ImageView>(R.id.btn_min)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete)
        val cekbok = view.findViewById<CheckBox>(R.id.cek)
        val txt_angka = view.findViewById<TextView>(R.id.txt_angka)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderData {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return HolderData(view)
    }

    override fun onBindViewHolder(holder: HolderData, position: Int) {

        val produk = data[position]
        val harga = Integer.valueOf(produk.harga)

        holder.tv_nama.text = produk.name
        holder.tv_harga.text = Helper().formatRupiah(harga * produk.jumlah)
            .format(Integer.valueOf(produk.harga))
        var jumlah = produk.jumlah
        holder.txt_angka.text = jumlah.toString()

        val imageUrl = Util.produkUrl + data[position].image
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.jagung)
            .error(R.drawable.jagung)
            .into(holder.tv_gambar)

        holder.btn_tambah.setOnClickListener {
//            if (jumlah >= 10) return@setOnClickListener
            jumlah++
            produk.jumlah = jumlah
            update(produk)
            holder.txt_angka.text = jumlah.toString()
            holder.tv_harga.text = Helper().formatRupiah((harga * jumlah).toString())
        }
        holder.btn_kurang.setOnClickListener {
            if (jumlah <= 1) return@setOnClickListener

            jumlah--
            produk.jumlah = jumlah
            update(produk)
            holder.txt_angka.text = jumlah.toString()
            holder.tv_harga.text = Helper().formatRupiah((harga * jumlah).toString())
        }
        holder.btn_delete.setOnClickListener {
            delete(produk)
            listener.onDelete(position)
        }
        holder.cekbok.isChecked = produk.selected
        holder.cekbok.setOnCheckedChangeListener { buttonView, isChecked ->
            produk.selected = isChecked
            update(produk)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface Listeners {
        fun onUpdate()
        fun onDelete(position: Int)
    }

    private fun update(produkData: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable {
            myDb!!.daoKeranjang().update(produkData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listener.onUpdate()
            })

    }

    private fun delete(produkData: Produk) {
        val myDb = MyDatabase.getInstance(activity)
        CompositeDisposable().add(Observable.fromCallable {
            myDb!!.daoKeranjang().delete(produkData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
            })

    }
}