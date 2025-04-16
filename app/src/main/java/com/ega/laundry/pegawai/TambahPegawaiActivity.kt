package com.ega.laundry.pegawai

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
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelPegawai
import com.google.firebase.database.FirebaseDatabase

class TambahPegawaiActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pegawai")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var idPegawai: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.tambahpegawai)
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
        idPegawai = intent.getStringExtra("idPegawai").toString()
        val judul = intent.getStringExtra("judul")
        val namaPegawai = intent.getStringExtra("namaPegawai")
        val alamatPegawai = intent.getStringExtra("alamatPegawai")
        val noHPPegawai = intent.getStringExtra("noHPPegawai")
        val idCabang = intent.getStringExtra("idCabang")
        tvJudul.text = judul
        etNama.setText(namaPegawai)
        etAlamat.setText(alamatPegawai)
        etNoHP.setText(noHPPegawai)
        etCabang.setText(idCabang)
        if(!tvJudul.text.equals(this.getString(R.string.tambahpegawai))){
            if(judul.equals(this.getString(R.string.editpegawai))){
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
        etAlamat.isEnabled = false
        etNoHP.isEnabled = false
        etCabang.isEnabled = false
    }

    fun hidup() {
        etNama.isEnabled = true
        etAlamat.isEnabled = true
        etNoHP.isEnabled = true
        etCabang.isEnabled = true
    }

    fun init(){
        tvJudul = findViewById(R.id.tvtambahkan_pegawai)
        etNama = findViewById(R.id.etnama_lengkappegawai)
        etAlamat = findViewById(R.id.etalamatpegawai)
        etNoHP = findViewById(R.id.etNoHppegawai)
        etCabang = findViewById(R.id.etcabangpegawai)
        btSimpan = findViewById(R.id.btsimpanpegawai)
    }

    fun cekValidasi() {
        val nama = etNama.text.toString()
        val alamat = etAlamat.text.toString()
        val noHp = etNoHP.text.toString()
        val cabang = etCabang.text.toString()

        if (nama.isEmpty()) {
            etNama.error = this.getString(R.string.validasi_namapegawai)
            Toast.makeText(this, this.getString(R.string.validasi_namapegawai),Toast.LENGTH_SHORT).show()
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = this.getString(R.string.validasi_alamat_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_alamat_pegawai),Toast.LENGTH_SHORT).show()
            etAlamat.requestFocus()
            return
        }
        if (noHp.isEmpty()) {
            etNoHP.error = this.getString(R.string.validasi_nohp_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_nohp_pegawai),Toast.LENGTH_SHORT).show()
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = this.getString(R.string.validasi_cabang_pegawai)
            Toast.makeText(this, this.getString(R.string.validasi_cabang_pegawai),Toast.LENGTH_SHORT).show()
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
        val pegawaiRef = database.getReference("pegawai").child(idPegawai.toString())
        // Buat Map untuk update data
        val updateData = mutableMapOf<String, Any>()
        updateData["namaPegawai"] = etNama.text.toString()
        updateData["alamatPegawai"] = etAlamat.text.toString()
        updateData["noHPPegawai"] = etNoHP.text.toString()
        updateData["idCabang"] = etCabang.text.toString()
        pegawaiRef.updateChildren(updateData).addOnSuccessListener {
            Toast.makeText(this, this.getString(R.string.edit_pegawai_sukses), Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, this.getString(R.string.edit_pegawai_gagal), Toast.LENGTH_SHORT).show()
        }
    }

    fun simpan() {
        val pegawaiBaru = myRef.push()
        val pegawaiid = pegawaiBaru.key
        val data = ModelPegawai(
            pegawaiid.toString(),
            etNama.text.toString(),
            etAlamat.text.toString(),
            etNoHP.text.toString(),
            etCabang.text.toString(),
        )
        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_pegawai_sukses),
                    Toast.LENGTH_SHORT
                )
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    this.getString(R.string.tambah_pegawai_gagal),
                    Toast.LENGTH_SHORT
                )
            }
    }
}