package com.ega.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.ega.laundry.cabang.DataCabangActivity
import com.ega.laundry.laporan.DataLaporanActivity
import com.ega.laundry.layanan.DataLayananActivity
import com.ega.laundry.pelanggan.DataPelangganActivity
import com.ega.laundry.pegawai.DataPegawaiActivity
import com.ega.laundry.tambahan.DataTambahanActivity
import com.ega.laundry.transaksi.DataTransaksiActivity
import java.text.SimpleDateFormat
import java.util.Locale

class main : AppCompatActivity() {

    private lateinit var tvHello: TextView
    private lateinit var tvService: TextView
    private lateinit var tvDate: TextView

    private lateinit var clPelanggan: ConstraintLayout
    private lateinit var clPegawai: ConstraintLayout
    private lateinit var clLayanan: ConstraintLayout
    private lateinit var clTransaksi: ConstraintLayout
    private lateinit var clTambahan: ConstraintLayout
    private lateinit var clLaporan: ConstraintLayout
    private lateinit var clCabang: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        init()
        setGreetingAndDate()
        setupNavigation()
    }

    private fun init() {
        tvHello = findViewById(R.id.tvhello)
        tvService = findViewById(R.id.tvservice)
        tvDate = findViewById(R.id.date)

        clPelanggan = findViewById(R.id.clPelanggan)
        clPegawai = findViewById(R.id.clPegawai)
        clLayanan = findViewById(R.id.clLayanan)
        clTransaksi = findViewById(R.id.clTransaksi)
        clTambahan = findViewById(R.id.clTambahan)
        clLaporan = findViewById(R.id.clLaporan)
        clCabang = findViewById(R.id.clCabang)
    }

    private fun setupNavigation() {
        clPelanggan.setOnClickListener {
            startActivity(Intent(this, DataPelangganActivity::class.java))
        }
        clPegawai.setOnClickListener {
            startActivity(Intent(this, DataPegawaiActivity::class.java))
        }
        clLayanan.setOnClickListener {
            startActivity(Intent(this, DataLayananActivity::class.java))
        }
        clTransaksi.setOnClickListener {
            startActivity(Intent(this, DataTransaksiActivity::class.java))
        }
        clTambahan.setOnClickListener {
            startActivity(Intent(this, DataTambahanActivity::class.java))
        }
        clLaporan.setOnClickListener {
            startActivity(Intent(this, DataLaporanActivity::class.java))
        }
        clCabang.setOnClickListener {
            startActivity(Intent(this, DataCabangActivity::class.java))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setGreetingAndDate() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Selamat Pagi"
            in 12..15 -> "Selamat Siang"
            in 16..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }

        val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val date = formatter.format(Calendar.getInstance().time)

        tvHello.text = greeting
        tvDate.text = date
    }
}
