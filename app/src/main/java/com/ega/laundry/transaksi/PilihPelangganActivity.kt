package com.ega.laundry.transaksi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.adapter.adapter_pilih_pelanggan
import com.ega.laundry.modeldata.ModelPelanggan
import com.google.firebase.database.*

class PilihPelangganActivity : AppCompatActivity() {

    private val TAG = "PilihPelanggan"
    private lateinit var rvPilihPelanggan: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var tvKosong: TextView
    private lateinit var adapter: adapter_pilih_pelanggan

    private val listPelanggan = ArrayList<ModelPelanggan>()
    private val filteredList = ArrayList<ModelPelanggan>()

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")
    private var idCabang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_pelanggan)

        rvPilihPelanggan = findViewById(R.id.rvPILIH_PELANGGAN)
        etSearch = findViewById(R.id.etSearch)
        tvKosong = findViewById(R.id.tvKosong)

        rvPilihPelanggan.layoutManager = LinearLayoutManager(this)

        adapter = adapter_pilih_pelanggan(filteredList)
        rvPilihPelanggan.adapter = adapter

        idCabang = intent.getStringExtra("idCabang") ?: ""

        getData()
        setupSearch()
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterList(query: String) {
        val lowerQuery = query.trim().lowercase()
        filteredList.clear()

        if (lowerQuery.isEmpty()) {
            filteredList.addAll(listPelanggan)
        } else {
            for (item in listPelanggan) {
                if (item.namaPelanggan?.lowercase()?.contains(lowerQuery) == true ||
                    item.alamatPelanggan?.lowercase()?.contains(lowerQuery) == true ||
                    item.noHPPelanggan?.lowercase()?.contains(lowerQuery) == true
                ) {
                    filteredList.add(item)
                }
            }
        }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        if (filteredList.isEmpty()) {
            tvKosong.visibility = View.VISIBLE
        } else {
            tvKosong.visibility = View.GONE
        }

        adapter.notifyDataSetChanged()
    }

    private fun getData() {
        val query: Query = if (idCabang.isNotEmpty()) {
            myRef.orderByChild("idCabang").equalTo(idCabang).limitToLast(100)
        } else {
            myRef.limitToLast(100)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPelanggan.clear()
                filteredList.clear()

                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val pelanggan = snap.getValue(ModelPelanggan::class.java)
                        if (pelanggan != null) {
                            listPelanggan.add(pelanggan)
                        }
                    }

                    filteredList.addAll(listPelanggan)
                    updateRecyclerView()
                } else {
                    tvKosong.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihPelangganActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
                tvKosong.visibility = View.VISIBLE
            }
        })
    }
}
