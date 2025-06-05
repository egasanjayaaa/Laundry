package com.ega.laundry
import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ega.laundry.cabang.DataCabangActivity
import com.ega.laundry.laporan.DataLaporanActivity
import com.ega.laundry.layanan.DataLayananActivity
import com.ega.laundry.modeldata.ModelLaporan
import com.ega.laundry.modeldata.ModelTambahan
import com.ega.laundry.pelanggan.DataPelangganActivity
import com.ega.laundry.pegawai.DataPegawaiActivity
import com.ega.laundry.tambahan.DataTambahanActivity
import com.ega.laundry.transaksi.DataTransaksiActivity
import java.text.SimpleDateFormat
import java.util.Locale

class main : AppCompatActivity() {
    lateinit var tvhello:TextView
    lateinit var tvservice: TextView
    lateinit var tvdate: TextView
    lateinit var clPelanggan: ConstraintLayout
    lateinit var clPegawai: ConstraintLayout
    lateinit var clLayanan: ConstraintLayout
    lateinit var clTransaksi: ConstraintLayout
    lateinit var clTambahan: ConstraintLayout
    lateinit var clLaporan: ConstraintLayout
    lateinit var clCabang: ConstraintLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        init()
        pewaktu()
        berpindah()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        tvhello = findViewById(R.id.tvhello)
        tvservice = findViewById(R.id.tvservice)
        tvdate = findViewById(R.id.date)
        clPelanggan = findViewById(R.id.clPelanggan)
        clPegawai = findViewById(R.id.clPegawai)
        clLayanan = findViewById(R.id.clLayanan)
        clTambahan = findViewById(R.id.clTambahan)
        clTransaksi = findViewById(R.id.clTransaksi)
        clLaporan = findViewById(R.id.clLaporan)
        clCabang = findViewById(R.id.clCabang)


    }
    fun  berpindah(){

        clPelanggan.setOnClickListener {
            val intent = Intent(this, DataPelangganActivity::class.java)
            startActivity(intent)
        }
        clPegawai.setOnClickListener {
            val intent = Intent(this, DataPegawaiActivity::class.java)
            startActivity(intent)
        }
        clLayanan.setOnClickListener {
            val intent = Intent(this, DataLayananActivity::class.java)
            startActivity(intent)
        }
        clTransaksi.setOnClickListener {
            val intent = Intent(this, DataTransaksiActivity::class.java)
            startActivity(intent)
        }
        clTambahan.setOnClickListener {
            val intent = Intent(this, DataTambahanActivity::class.java)
            startActivity(intent)
        }
        clLaporan.setOnClickListener {
            val intent = Intent(this, DataLaporanActivity::class.java)
            startActivity(intent)
        }
        clCabang.setOnClickListener {
            val intent = Intent(this, DataCabangActivity::class.java)
            startActivity(intent)
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun pewaktu() {
        val jam = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val hth = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val result = when (jam) {
            in 0..11 -> "Selamat Pagi"
            in 12..15 -> "Selamat Siang"
            in 16..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
        tvhello.text = result
        tvdate.text = hth.format(Calendar.getInstance().time)
    }
}