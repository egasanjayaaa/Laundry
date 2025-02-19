//package com.ega
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.ega.laundry.pegawai.DataPegawaiActivity
//import com.example.laundry.Pelanggan.DataPelangganActivity
//import com.ega.laundry.R
//import java.time.LocalDate
//import java.time.LocalTime
//import java.time.format.DateTimeFormatter
//
//class MainActivity : AppCompatActivity() {
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        val tvDate = findViewById<TextView>(R.id.tvhello)
//        tvDate.text = getCurrentDate()
//
//        val tvGreeting = findViewById<TextView>(R.id.tvservice)
//        tvGreeting.text = getGreetingMessage()
//
//        val pelangganMenu = findViewById<LinearLayout>(R.id.clPelanggan)
//        pelangganMenu.setOnClickListener {
//            val intent = Intent(this, DataPelangganActivity::class.java)
//            startActivity(intent)
//        }
//
//        val pegawaiMenu = findViewById<LinearLayout>(R.id.pegawaimenu)
//        pegawaiMenu.setOnClickListener {
//            val intent = Intent(this, DataPegawaiActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//    private fun getGreetingMessage(): String {
//        val currentTime = LocalTime.now()
//        return when {
//            currentTime.hour in 5..10 -> "Selamat Pagi"
//            currentTime.hour in 11..14 -> "Selamat Siang"
//            currentTime.hour in 15..18 -> "Selamat Sore"
//            else -> "Selamat Malam"
//        }
//    }
//
//    private fun getCurrentDate(): String {
//        val currentDate = LocalDate.now()
//        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
//        return currentDate.format(formatter)
//        }
//}

package com.ega.laundry

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ega.laundry.R
import com.ega.laundry.pegawai.DataPegawaiActivity
import com.ega.laundry.pelanggan.DataPelangganActivity
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var tvhello:TextView
    lateinit var tvservice: TextView
    lateinit var tvdate: TextView
    lateinit var clPelanggan: ConstraintLayout



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
    }
    fun  berpindah(){

//        clPegawai.setOnClickListener {
//            val intent = Intent(this, DataPegawaiActivity::class.java)
//            startActivity(intent)
//        }
        clPelanggan.setOnClickListener {
            val intent = Intent(this, DataPelangganActivity::class.java)
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