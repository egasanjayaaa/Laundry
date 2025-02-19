package com.ega.laundry.pelanggan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelPelanggan
import com.google.firebase.database.FirebaseDatabase

class TambahPelangganActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")

    private lateinit var tvJudul: TextView
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHP: EditText
    private lateinit var btSimpan: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambah_pelanggan) // ✅ Pindahkan sebelum init()

        init() // ✅ Panggil setelah setContentView()

        btSimpan.setOnClickListener {
            cekValidasi()
        }
    }

    private fun init() {
        tvJudul = findViewById(R.id.tvtambahkan_pelanggan)
        etNama = findViewById(R.id.etnama_lengkap)
        etAlamat = findViewById(R.id.etalamat)
        etNoHP = findViewById(R.id.etNoHp)
        btSimpan = findViewById(R.id.btsimpan)
    }

    private fun cekValidasi() {
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val nohp = etNoHP.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = getString(R.string.validasi_nama_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_nama_pelanggan), Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }

        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.validasi_alamat_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_alamat_pelanggan), Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }

        if (nohp.isEmpty()) {
            etNoHP.error = getString(R.string.validasi_nohp_pelanggan)
            Toast.makeText(this, getString(R.string.validasi_nohp_pelanggan), Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }

        simpanData(nama, alamat, nohp)
    }

    private fun simpanData(nama: String, alamat: String, nohp: String) {
        val pelangganBaru = myRef.push()
        val pelangganID = pelangganBaru.key ?: ""
        val data = ModelPelanggan(pelangganID, nama, alamat, nohp)

        pelangganBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Gagal menyimpan: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", "Gagal menyimpan data: ${error.message}")
            }
    }
}
