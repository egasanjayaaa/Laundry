package com.ega.laundry.pelanggan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ega.adapter.adapter_data_pelanggan
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelPelanggan
import com.ega.laundry.pelanggan.TambahPelangganActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataPelangganActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rvDATA_PELANGGAN: RecyclerView
    lateinit var fabDATA_PENGGUNA_Tambah: FloatingActionButton
    lateinit var pelangganList: ArrayList<ModelPelanggan>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pelanggan)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_PELANGGAN.layoutManager = layoutManager
        rvDATA_PELANGGAN.setHasFixedSize(true)

        pelangganList = arrayListOf<ModelPelanggan>()
        getDate()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_PENGGUNA_Tambah.setOnClickListener {
            val intent = Intent(this, TambahPelangganActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        rvDATA_PELANGGAN = findViewById(R.id.rvDATA_PELANGGAN)
        fabDATA_PENGGUNA_Tambah = findViewById(R.id.fabDATA_PENGGUNA_Tambah)
    }

    fun getDate() {
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pelangganList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(ModelPelanggan::class.java)
                        if (pegawai != null) {  // Cek nullability agar tidak crash
                            pelangganList.add(pegawai as ModelPelanggan)
                        }
                    }
                    val adapter = adapter_data_pelanggan(pelangganList)
                    rvDATA_PELANGGAN.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPelangganActivity, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show()
            }
            })
      }
}

