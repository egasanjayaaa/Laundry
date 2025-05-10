package com.ega.laundry.layanan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.adapter.adapter_data_layanan
import com.ega.laundry.modeldata.ModelLayanan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataLayananActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")
    lateinit var rvDATA_LAYANAN: RecyclerView
    lateinit var fabDATA_LAYANAN_Tambah: FloatingActionButton
    lateinit var layananList: ArrayList<ModelLayanan>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_layanan)

        init()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_LAYANAN.layoutManager = layoutManager
        rvDATA_LAYANAN.setHasFixedSize(true)

        layananList = arrayListOf<ModelLayanan>()
        getDate()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_LAYANAN_Tambah.setOnClickListener {
            val intent = Intent(this, TambahLayananActivity::class.java)
            startActivity(intent)
        }
    }

    fun init() {
        rvDATA_LAYANAN = findViewById(R.id.rvDATA_LAYANAN)
        fabDATA_LAYANAN_Tambah = findViewById(R.id.fabDATA_LAYANAN_Tambah)
    }

    fun getDate() {
        val query = myRef.orderByChild("idlayanan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    layananList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                        if (layanan != null) {
                            layananList.add(layanan)
                        }
                    }
                    val adapter = adapter_data_layanan(layananList)
                    rvDATA_LAYANAN.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLayananActivity, "Data Gagal Dimuat", Toast.LENGTH_SHORT).show()
            }
        })
    }
}