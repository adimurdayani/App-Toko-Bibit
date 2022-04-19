package com.uci.mybibit.model

class Transaksi {
    var id = 0
    var user_id = ""
    var kode_payment = ""
    var kode_trx = ""
    var total_item = ""
    var total_harga = ""
    var kode_unik = 0
    var status = ""
    var resi = ""
    var kurir = ""
    var phone = ""
    var name = ""
    var detail_lokasi = ""
    var deskripsi = ""
    var metode = ""
    var expired_at = ""
    var created_at = ""
    var updated_at = ""
    var jasa_pengiriman = ""
    var ongkir = ""
    var total_transfer = ""
    var bank = ""
    var toko_id = 0

    var details = ArrayList<DetailTransaksi>()

}
