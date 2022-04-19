package com.uci.mybibit.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thecode.aestheticdialogs.*
import com.uci.mybibit.R
import com.uci.mybibit.activity.LoginActivity
import com.uci.mybibit.activity.PengirimanActivity
import com.uci.mybibit.adapter.AdapterKeranjang
import com.uci.mybibit.helper.Helper
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.Produk
import com.uci.mybibit.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FragmentKeranjang : Fragment() {

    lateinit var btn_delete: ImageView
    lateinit var btn_beli: LinearLayout
    lateinit var total: TextView
    lateinit var rc_data: RecyclerView
    lateinit var cekall: CheckBox
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_keranjang, container, false)
        init(view)
        myDb = MyDatabase.getInstance(requireActivity())!!
        s = SharedPref(requireActivity())
        setButton()
        return view
    }

    lateinit var adapter: AdapterKeranjang
    var listProduk = ArrayList<Produk>()
    private fun displayProduk() {
        listProduk = myDb.daoKeranjang().getAll() as ArrayList

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter =
            AdapterKeranjang(requireActivity(), listProduk, object : AdapterKeranjang.Listeners {
                override fun onUpdate() {
                    hitungTotal()
                }

                override fun onDelete(position: Int) {
                    listProduk.removeAt(position)
                    adapter.notifyDataSetChanged()
                    hitungTotal()
                }

            })
        rc_data.adapter = adapter
        rc_data.layoutManager = layoutManager
    }

    var totalHarga = 0
    fun hitungTotal() {
        val listProduk = myDb.daoKeranjang().getAll() as ArrayList
        totalHarga = 0
        var isSelectedAll = true
        for (produk in listProduk) {
            if (produk.selected) {
                val harga = Integer.valueOf(produk.harga)
                totalHarga += (harga * produk.jumlah)
            } else {
                isSelectedAll = false
            }
        }
        cekall.isChecked = isSelectedAll
        total.text = Helper().formatRupiah(totalHarga)
    }

    private fun setButton() {
        btn_delete.setOnClickListener {
            setDialog("Apakah Anda Yakin?")
        }
        btn_beli.setOnClickListener {

            if (s.getStatusLogin()) {
                var isThereProduk = false
                for (p in listProduk) {
                    if (p.selected) isThereProduk = true
                }

                if (isThereProduk) {
                    val intent = Intent(activity, PengirimanActivity::class.java)
                    intent.putExtra("extra", "" + totalHarga)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Tidak ada produk yang dipilih",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
            }

        }
        cekall.setOnClickListener {
            for (i in listProduk.indices) {
                val produk = listProduk[i]
                produk.selected = cekall.isChecked
                listProduk[i] = produk
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun delete(produkData: ArrayList<Produk>) {
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoKeranjang().delete(produkData)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listProduk.clear()
                listProduk.addAll(myDb.daoKeranjang().getAll() as ArrayList)
                adapter.notifyDataSetChanged()
            })

    }

    private fun init(view: View) {
        btn_delete = view.findViewById(R.id.btn_delete)
        btn_beli = view.findViewById(R.id.btn_beli2)
        total = view.findViewById(R.id.total_harga)
        rc_data = view.findViewById(R.id.rc_data)
        cekall = view.findViewById(R.id.cekall)
    }

    fun setDialog(pesan:String){
        AestheticDialog.Builder(requireActivity(), DialogStyle.FLAT, DialogType.WARNING)
            .setTitle(pesan)
            .setMessage("Keranjang akan terhapus permanen!")
            .setCancelable(true)
            .setDarkMode(true)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    val listDelete = ArrayList<Produk>()
                    for (p in listProduk) {
                        if (p.selected) listDelete.add(p)
                    }
                    delete(listDelete)
                }
            })
            .show()
    }

    override fun onResume() {
        displayProduk()
        hitungTotal()
        super.onResume()
    }
}