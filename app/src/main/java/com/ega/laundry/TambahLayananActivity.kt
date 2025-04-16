package com.ega.laundry.layanan

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelLayanan

class TambahLayananActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etHarga: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var idLayanan: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tambah_layanan)
        init()
        getDate()
        btSimpan.setOnClickListener{
            cekValidasi()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun getDate() {
        idLayanan = intent.getStringExtra("idLayanan").toString()
        val judul = intent.getStringExtra("judul")
        val namaLayanan = intent.getStringExtra("namaLayanan")
        val hargaLayanan = intent.getStringExtra("hargaLayanan")
        val idCabang = intent.getStringExtra("idCabang")
        tvJudul.text = judul
        etNama.setText(namaLayanan)
        etHarga.setText(hargaLayanan)
        etCabang.setText(idCabang)
        if(!tvJudul.text.equals(this.getString(R.string.tambahlayanan))){
            if(judul.equals(this.getString(R.string.editlayanan))){
                mati()
                btSimpan.text="Sunting"
            }
        }else{
            hidup()
            etNama.requestFocus()
            btSimpan.text="Simpan"
        }
    }

    fun mati(){
        etNama.isEnabled = false
        etHarga.isEnabled = false
        etCabang.isEnabled = false
    }

    fun hidup() {
        etNama.isEnabled = true
        etHarga.isEnabled = true
        etCabang.isEnabled = true
    }

    fun init(){
        tvJudul = findViewById(R.id.tvtambahkan_layanan)
        etNama = findViewById(R.id.etnama_lengkaplayanan)
        etHarga = findViewById(R.id.ethargalayanan)
        etCabang = findViewById(R.id.etcabanglayanan)
        btSimpan = findViewById(R.id.btsimpanlayanan)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val harga = etHarga.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_nama_layanan)
            Toast.makeText(this, this.getString(R.string.validasi_nama_layanan),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (harga.isEmpty()) {
            etHarga.error = this.getString(R.string.validasi_harga_layanan)
            Toast.makeText(this, this.getString(R.string.validasi_harga_layanan),Toast.LENGTH_SHORT).show()
            etHarga.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_layanan)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_layanan),Toast.LENGTH_SHORT).show()
            etCabang.requestFocus()
            return
        }
        if (btSimpan.text.equals(this.getString(R.string.button_simpan))) {
            simpan()
        } else if (btSimpan.text.equals(this.getString(R.string.sunting))) {
            hidup()
            etNama.requestFocus()
            btSimpan.text = "Perbarui"
        } else if (btSimpan.text.equals(this.getString(R.string.perbarui))) {
            update()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update() {
        val pegawaiRef = database.getReference("pegawai").child(idLayanan.toString())
        // Buat Map untuk update data
        val updateData = mutableMapOf<String, Any>()
        updateData["namaLayanan"] = etNama.text.toString()
        updateData["hargaLayanan"] = etHarga.text.toString()
        updateData["idCabang"] = etCabang.text.toString()
        pegawaiRef.updateChildren(updateData).addOnSuccessListener {
            Toast.makeText(this, this.getString(R.string.edit_layanan_sukses), Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, this.getString(R.string.edit_layanan_gagal), Toast.LENGTH_SHORT).show()
        }
    }

    fun simpan() {
        val layananBaru = myRef.push()
        val layananid = layananBaru.key
        val data = ModelLayanan(
            layananid.toString(),
            etNama.text.toString(),
            etHarga.text.toString(),
            etCabang.text.toString(),
        )
        layananBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_layanan_sukses),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_layanan_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }
}