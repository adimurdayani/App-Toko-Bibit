package com.uci.mybibit.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.thecode.aestheticdialogs.*
import com.uci.mybibit.R
import com.uci.mybibit.api.ApiConfigAlamat
import com.uci.mybibit.helper.SharedPref
import com.uci.mybibit.model.Alamat
import com.uci.mybibit.model.ModelAlamat
import com.uci.mybibit.model.ResponsModel
import com.uci.mybibit.room.MyDatabase
import com.uci.mybibit.util.ApiKey
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TambahAlamat : AppCompatActivity() {
    lateinit var btn_kembali: ImageView
    lateinit var sp_prov: Spinner
    lateinit var sp_kota: Spinner
    lateinit var progress: ProgressBar
    lateinit var div_provinsi: RelativeLayout
    lateinit var div_kota: RelativeLayout
    lateinit var btn_simpan: CardView
    lateinit var l_name: TextInputLayout
    lateinit var e_name: TextInputEditText
    lateinit var l_nohp: TextInputLayout
    lateinit var e_nohp: TextInputEditText
    lateinit var l_alamat: TextInputLayout
    lateinit var e_alamat: TextInputEditText
    lateinit var l_alamat2: TextInputLayout
    lateinit var e_alamat2: TextInputEditText
    lateinit var l_kodepos: TextInputLayout
    lateinit var e_kodepos: TextInputEditText
    lateinit var progress2: ProgressBar

    var provinsi = ModelAlamat.Provinsi()
    var kota = ModelAlamat.Provinsi()
    var kecamatan = ModelAlamat()
    lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_alamat)
        s = SharedPref(this)
        setButton()
        getProvinsi()
        getButton()
        cekvalidasi()
    }

    fun getButton() {
        btn_kembali.setOnClickListener {
            onBackPressed()
        }
        btn_simpan.setOnClickListener {
            if (validasi()) {
                simpan()
            }
        }
    }

    fun simpan() {
        if (provinsi.province_id == "0") {
            Toast.makeText(this, "Silahkan pilih provinsi", Toast.LENGTH_SHORT).show()
            return
        }
        if (kota.city_id == "0") {
            Toast.makeText(this, "Silahkan pilih kota", Toast.LENGTH_SHORT).show()
            return
        }
        val alamat = Alamat()
        alamat.id = s.getUser()!!.id
        alamat.name = e_name.text.toString()
        alamat.type = e_alamat.text.toString()
        alamat.phone = e_nohp.text.toString()
        alamat.alamat = e_alamat2.text.toString()
        alamat.kodepos = e_kodepos.text.toString()

        alamat.id_provinsi = Integer.valueOf(provinsi.province_id)
        alamat.provinsi = provinsi.province
        alamat.id_kota = Integer.valueOf(kota.city_id)
        alamat.kota = kota.city_name
        insert(alamat)
    }

    fun getProvinsi() {
        progress.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getProvinsi(ApiKey.key)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    if (response.isSuccessful) {
                        progress.visibility = View.GONE
                        div_provinsi.visibility = View.VISIBLE

                        val res = response.body()!!
                        val arrayString = ArrayList<String>()
                        arrayString.add("Pilih Provinsi")

                        val listPovinsi = res.rajaongkir.results
                        for (prov in listPovinsi) {
                            arrayString.add(prov.province)
                        }
                        val adapter = ArrayAdapter<Any>(
                            this@TambahAlamat,
                            R.layout.item_spinner,
                            arrayString.toTypedArray()
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sp_prov.adapter = adapter
                        sp_prov.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long,
                                ) {
                                    if (position != 0) {
                                        provinsi = listPovinsi[position - 1]
                                        val idProv = provinsi.province_id
                                        getKota(idProv)
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    TODO("Not yet implemented")
                                }

                            }

                    } else {
                        Log.d("Error", "gagal memuat data" + response.message())
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    Log.d("Error", "gagal memuat data" + t.message)
                    error(t.message.toString())
                }

            })
    }

    fun getKota(id: String) {
        progress.visibility = View.VISIBLE
        ApiConfigAlamat.instanceRetrofit.getKota(ApiKey.key, id)
            .enqueue(object : Callback<ResponsModel> {
                override fun onResponse(
                    call: Call<ResponsModel>,
                    response: Response<ResponsModel>,
                ) {
                    if (response.isSuccessful) {
                        progress.visibility = View.GONE
                        div_kota.visibility = View.VISIBLE

                        val res = response.body()!!
                        val arrayString = ArrayList<String>()
                        val listArry = res.rajaongkir.results
                        arrayString.add("Pilih Kota")
                        for (kota in listArry) {
                            arrayString.add(kota.city_name + " " + kota.postal_code)
                        }
                        val adapter = ArrayAdapter<Any>(
                            this@TambahAlamat,
                            R.layout.item_spinner,
                            arrayString.toTypedArray()
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        sp_kota.adapter = adapter
                        sp_kota.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long,
                                ) {
                                    if (position != 0) {
                                        kota = listArry[position - 1]
                                        val kodepos = kota.postal_code
                                        e_kodepos.setText(kodepos)
                                    }
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {

                                }

                            }
                    } else {
                        Log.d("Error", "gagal memuat data" + response.message())
                        error(response.message())
                    }
                }

                override fun onFailure(call: Call<ResponsModel>, t: Throwable) {
                    error(t.message.toString())
                }

            })
    }
    private fun insert(data: Alamat) {
        progress2.visibility = View.VISIBLE
        val myDb = MyDatabase.getInstance(this)!!
        if (myDb.daoAlamat().getBystatus(true) == null) {
            progress2.visibility = View.GONE
            data.isSelected = true
        }
        CompositeDisposable().add(Observable.fromCallable { myDb.daoAlamat().insert(data) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progress2.visibility = View.GONE
                sukses("Alamat telah berhasil disimpan")
                onBackPressed()
                for (alamat in myDb.daoAlamat().getAll()) {
                    Log.d(
                        "Alamat",
                        "nama:  " + alamat.name + " - " + alamat.alamat + " - " + alamat.kota + " - " + alamat.id_kota
                    )
                }
            })
    }

    private fun cekvalidasi() {
        e_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_name.text.toString().isEmpty()) {
                    l_name.isErrorEnabled = false
                } else if (e_name.text.toString().isNotEmpty()) {
                    l_name.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_nohp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_nohp.text.toString().isEmpty()) {
                    l_nohp.isErrorEnabled = false
                } else if (e_nohp.text.toString().isNotEmpty()) {
                    l_nohp.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_alamat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_alamat.text.toString().isEmpty()) {
                    l_alamat.isErrorEnabled = false
                } else if (e_alamat.text.toString().isNotEmpty()) {
                    l_alamat.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_alamat2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_alamat2.text.toString().isEmpty()) {
                    l_alamat2.isErrorEnabled = false
                } else if (e_alamat2.text.toString().isNotEmpty()) {
                    l_alamat2.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        e_kodepos.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (e_kodepos.text.toString().isEmpty()) {
                    l_kodepos.isErrorEnabled = false
                } else if (e_kodepos.text.toString().isNotEmpty()) {
                    l_kodepos.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun validasi(): Boolean {
        if (e_name.text.toString().isEmpty()) {
            l_name.isErrorEnabled = true
            l_name.error = "Kolom nama tidak boleh kosong!"
            e_name.requestFocus()
            return false
        }
        if (e_nohp.text.toString().isEmpty()) {
            l_nohp.isErrorEnabled = true
            l_nohp.error = "Kolom phone tidak boleh kosong!"
            e_nohp.requestFocus()
            return false
        }
        if (e_alamat.text.toString().isEmpty()) {
            l_alamat.isErrorEnabled = true
            l_alamat.error = "Kolom email tidak boleh kosong!"
            e_alamat.requestFocus()
            return false
        }
        if (e_alamat2.text.toString().isEmpty()) {
            l_alamat2.isErrorEnabled = true
            l_alamat2.error = "Kolom email tidak boleh kosong!"
            e_alamat2.requestFocus()
            return false
        }
        if (e_kodepos.text.toString().isEmpty()) {
            l_kodepos.isErrorEnabled = true
            l_kodepos.error = "Kolom kode pos tidak boleh kosong!"
            e_kodepos.requestFocus()
            return false
        }

        return true
    }

    private fun setButton() {
        btn_kembali = findViewById(R.id.btn_kembali)
        sp_prov = findViewById(R.id.sp_provinsi)
        sp_kota = findViewById(R.id.sp_kota)
        progress = findViewById(R.id.progressbar)
        div_provinsi = findViewById(R.id.div_provinsi)
        div_kota = findViewById(R.id.div_kota)
        btn_simpan = findViewById(R.id.btn_simpan)
        l_name = findViewById(R.id.l_name)
        e_name = findViewById(R.id.e_name)
        l_nohp = findViewById(R.id.l_nohp)
        e_nohp = findViewById(R.id.e_nohp)
        l_alamat = findViewById(R.id.l_alamat)
        e_alamat = findViewById(R.id.e_alamat)
        l_alamat2 = findViewById(R.id.l_alamat2)
        e_alamat2 = findViewById(R.id.e_alamat2)
        l_kodepos = findViewById(R.id.l_kodepos)
        e_kodepos = findViewById(R.id.e_kodepos)
        progress2 = findViewById(R.id.progress)

        e_name.setText(s.getUser()!!.name)
        e_nohp.setText(s.getUser()!!.phone)
    }

    fun sukses(pesan: String) {
        AestheticDialog.Builder(this, DialogStyle.FLAT, DialogType.SUCCESS)
            .setTitle("Sukses")
            .setMessage(pesan)
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SLIDE_DOWN)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            })
            .show()
    }

    fun error(pesan: String) {
        AestheticDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Error Koneksi")
            .setMessage(pesan)
            .show()
    }

}