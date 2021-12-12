package com.uci.mybibit.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.thecode.aestheticdialogs.AestheticDialog
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import com.uci.mybibit.R
import com.uci.mybibit.activity.*
import com.uci.mybibit.helper.SharedPref


class FragmentAkun : Fragment() {
    lateinit var btn_logout: TextView
    lateinit var s: SharedPref
    lateinit var tv_nama: TextView
    lateinit var tv_email: TextView
    lateinit var tv_phone: TextView
    lateinit var btn_alamat: RelativeLayout
    lateinit var btn_ubahpassword: RelativeLayout
    lateinit var btn_ubahprofil: RelativeLayout
    lateinit var btn_tentang: RelativeLayout
    lateinit var btn_bantuan: RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_akun, container, false)
        init(view)
        s = SharedPref(requireActivity())
        setButton()
        setData()
        return view
    }

    private fun setButton() {
        val dialog = AlertDialog.Builder(requireActivity())
        btn_logout.setOnClickListener {
            dialog.setTitle("Apakah anda yakin ingin keluar?")
            dialog.setIcon(R.drawable.ic_baseline_add_alert_24)
            dialog.setCancelable(true)
            dialog.setNegativeButton("Tidak", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            dialog.setPositiveButton("Ya", DialogInterface.OnClickListener { dialog, which ->
                s.setStatusLogin(false)
                val intent = Intent(requireActivity(), HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                requireActivity().finish()
            })
            dialog.show()
        }

        btn_alamat.setOnClickListener {
            startActivity(Intent(requireActivity(), TambahAlamat::class.java))
        }
        btn_ubahpassword.setOnClickListener {
            startActivity(Intent(requireActivity(), UbahPassword::class.java))
        }
        btn_ubahprofil.setOnClickListener {
            startActivity(Intent(requireActivity(), UbahProfile::class.java))
        }
        btn_tentang.setOnClickListener {
            startActivity(Intent(requireActivity(), Tentang::class.java))
        }
        btn_bantuan.setOnClickListener {
            startActivity(Intent(requireActivity(), Bantuan::class.java))
        }
    }

    private fun setData() {
        if (s.getUser() == null) {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            return
        }
        val user = s.getUser()!!

        tv_nama.text = user.name
        tv_email.text = user.email
        tv_phone.text = user.phone
    }

    private fun init(view: View) {
        btn_logout = view.findViewById(R.id.btn_logout)
        tv_nama = view.findViewById(R.id.nama)
        tv_email = view.findViewById(R.id.email)
        tv_phone = view.findViewById(R.id.phone)
        btn_alamat = view.findViewById(R.id.btn_alamat)
        btn_ubahpassword = view.findViewById(R.id.btn_ubahpassword)
        btn_ubahprofil = view.findViewById(R.id.btn_ubahprofil)
        btn_tentang = view.findViewById(R.id.btn_tentang)
        btn_bantuan = view.findViewById(R.id.btn_bantuan)
    }

    fun error(pesan: String) {
        AestheticDialog.Builder(requireActivity(), DialogStyle.CONNECTIFY, DialogType.ERROR)
            .setTitle("Title")
            .setMessage(pesan)
            .show()
    }
}