package com.uci.mybibit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Adapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thecode.aestheticdialogs.*
import com.uci.mybibit.R
import com.uci.mybibit.adapter.AdapterAlamat
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.Alamat
import com.uci.mybibit.model.Produk
import com.uci.mybibit.room.MyDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListAlamat : AppCompatActivity() {
    lateinit var btn_alamat: LinearLayout
    lateinit var btn_kembali: ImageView
    lateinit var div_pesan: LinearLayout
    lateinit var rc_data: RecyclerView
    lateinit var btn_delete: ImageView
    lateinit var cekall: CheckBox
    lateinit var myDb: MyDatabase
    lateinit var s: SharedPref
    lateinit var adapter: AdapterAlamat
    var listAlamat = ArrayList<Alamat>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_alamat)
        s = SharedPref(this)
        myDb = MyDatabase.getInstance(this)!!
        setButton()
    }

    private fun displayAlamat() {
        val id = s.getUser()!!.id
        listAlamat = myDb.daoAlamat().getIdAlamat(id) as ArrayList

        if (listAlamat.isEmpty()) div_pesan.visibility = View.VISIBLE
        else div_pesan.visibility = View.GONE

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        adapter = AdapterAlamat(listAlamat, object : AdapterAlamat.Listeners {
            override fun onClicked(data: Alamat) {
                if (myDb.daoAlamat().getBystatus(true) != null) {
                    val alamatActive = myDb.daoAlamat().getBystatus(true)!!
                    alamatActive.isSelected = false
                    updateActive(alamatActive, data)
                }
            }
        })
        rc_data.adapter = adapter
        rc_data.layoutManager = layoutManager
    }

    private fun updateActive(dataActive: Alamat, dataNonActive: Alamat) {
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoAlamat().update(dataActive)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateNonActive(dataNonActive)
            })

    }

    private fun updateNonActive(data: Alamat) {
        data.isSelected = true
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoAlamat().update(data)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onBackPressed()
            })

    }

    private fun setButton() {
        btn_alamat = findViewById(R.id.btn_alamat)
        btn_kembali = findViewById(R.id.btn_kembali)
        div_pesan = findViewById(R.id.div_pesan)
        rc_data = findViewById(R.id.rc_data)
        btn_delete = findViewById(R.id.btn_delete)
        cekall = findViewById(R.id.cekall)

        btn_alamat.setOnClickListener {
            startActivity(Intent(this, TambahAlamat::class.java))
        }
        btn_kembali.setOnClickListener {
            onBackPressed()
        }

        btn_delete.setOnClickListener {
            setDialog("Apakah Anda Yakin?")
        }

        cekall.setOnClickListener {
            for (i in listAlamat.indices) {
                val alamat = listAlamat[i]
                alamat.isSelected = cekall.isChecked
                listAlamat[i] = alamat
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun delete(dataAlamat: ArrayList<Alamat>) {
        CompositeDisposable().add(Observable.fromCallable {
            myDb.daoAlamat().delete(dataAlamat)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                listAlamat.clear()
                listAlamat.addAll(myDb.daoAlamat().getIdAlamat(s.getUser()!!.id) as ArrayList)
                adapter.notifyDataSetChanged()
            })

    }

    fun setDialog(pesan:String){
        AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.WARNING)
            .setTitle(pesan)
            .setMessage("List alamat akan terhapus permanen!")
            .setCancelable(true)
            .setDarkMode(true)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                    val listDelete = ArrayList<Alamat>()
                    for (p in listAlamat) {
                        if (p.isSelected) listDelete.add(p)
                    }
                    delete(listDelete)
                }
            })
            .show()
    }

    override fun onResume() {
        displayAlamat()
        super.onResume()
    }
}