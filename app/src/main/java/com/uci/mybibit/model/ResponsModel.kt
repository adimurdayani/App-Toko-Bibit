package com.uci.mybibit.model

class ResponsModel {
    var success = 0
    lateinit var message: String
    var data = User()
    var produk_id: ArrayList<ProdukAll> = ArrayList()
    var produk: ArrayList<ProdukAll> = ArrayList()
    var transaksis: ArrayList<Transaksi> = ArrayList()
    var user: ArrayList<User> = ArrayList()
    var daftar_toko: ArrayList<DaftarToko> = ArrayList()

    var rajaongkir = ModelAlamat()
    var transaksi = Transaksi()

    var provinsi: ArrayList<ModelAlamat> = ArrayList()
    var kota_kabupaten: ArrayList<ModelAlamat> = ArrayList()
    var kecamatan: ArrayList<ModelAlamat> = ArrayList()
}
