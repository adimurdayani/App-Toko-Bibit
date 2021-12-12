package com.uci.mybibit.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "keranjang") // the name of tabel
public class Produk implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    public int idTb;

    public int id;
    public String name;
    public String harga;
    public String deskripsi;
    public int kategori_id;
    public String image;
    public String created_at;
    public String updated_at;
    public String stok;
    public String berat;
    public int user_id;

    public int jumlah = 1;
    public boolean selected = true;
}
