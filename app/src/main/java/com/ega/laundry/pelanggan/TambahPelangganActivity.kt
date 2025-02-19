package com.ega.laundry.pelanggan

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ega.laundry.R
import com.google.firebase.database.FirebaseDatabase

class TambahPelangganActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang : EditText
    lateinit var btSimpan: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        setContentView(R.layout.tambah_pelanggan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init(){
        tvJudul = findViewById(R.id.tvtambahkan_pelanggan)
        etNama = findViewById(R.id.etnama_lengkap)
        etAlamat = findViewById(R.id.etalamat)
        etNoHP = findViewById(R.id.etNoHp)
        btSimpan = findViewById(R.id.btsimpan)
    }

    fun cekValidasi(){
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val nohp = etNoHP.text.toString()
        val cabang = etCabang.text.toString()
        //Validasi data
        if (nama.isEmpty()) {
            etNama.error=this.getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error=this.getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (nohp.isEmpty()) {
            etNoHP.error=this.getString(R.string.validasi_nohp_pelanggan)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_pelanggan), Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }

        }
}
