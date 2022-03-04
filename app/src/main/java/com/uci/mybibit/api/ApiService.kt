package com.uci.mybibit.api

import com.uci.mybibit.model.Checkout
import com.uci.mybibit.model.ResponsModel
import com.uci.mybibit.model.rajaongkir.ResponsOngkir
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("fcm") fcmString: String
    ): Call<ResponsModel>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcm") fcmString: String
    ): Call<ResponsModel>

    @POST("checkout")
    fun checkout(
        @Body data: Checkout
    ): Call<ResponsModel>

    @GET("produk")
    fun produk(): Call<ResponsModel>

    @GET("daftar_toko")
    fun daftar_toko(): Call<ResponsModel>

    @GET("produk/kategori/{kategori_id}")
    fun produkId(
        @Path("kategori_id") kategori_id: Int
    ): Call<ResponsModel>

    @GET("produk/produk_user/{user_id}")
    fun produk_user(
        @Path("user_id") user_id: Int
    ): Call<ResponsModel>

    @GET("produk/kategori_nolimit/{kategori_id}")
    fun produkIdAll(
        @Path("kategori_id") kategori_id: Int
    ): Call<ResponsModel>

    @GET("province")
    fun getProvinsi(
        @Header("key") key: String
    ): Call<ResponsModel>

    @GET("city")
    fun getKota(
        @Header("key") key: String,
        @Query("province") id: String
    ): Call<ResponsModel>

    @GET("kecamatan")
    fun getKecamatan(
        @Query("id_kota") id: Int
    ): Call<ResponsModel>

    @FormUrlEncoded
    @POST("cost")
    fun ongkir(
        @Header("key") key: String,
        @Field("origin") origin: String,
        @Field("destination") destination: String,
        @Field("weight") weight: Int,
        @Field("courier") courier: String
    ): Call<ResponsOngkir>

    @GET("checkout/user/{id}")
    fun getRiwayat(
        @Path("id") id: Int
    ): Call<ResponsModel>

    @GET("user/{id}")
    fun getIdUser(
        @Path("id") id: Int
    ): Call<ResponsModel>

    @POST("checkout/batal/{id}")
    fun batalcheckout(
        @Path("id") id: Int
    ): Call<ResponsModel>

    @Multipart
    @POST("checkout/upload/{id}")
    fun uploadbukti(
        @Path("id") id: Int,
        @Part image: MultipartBody.Part
    ): Call<ResponsModel>
}