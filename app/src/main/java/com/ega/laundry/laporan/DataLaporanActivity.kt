package com.ega.laundry.laporan

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.ega.laundry.R
import com.ega.laundry.adapter.adapter_data_laporan
import com.ega.laundry.modeldata.StatusLaporan
import com.ega.laundry.modeldata.ModelLaporan

class DataLaporanActivity : AppCompatActivity(), adapter_data_laporan.OnStatusChangeListener, adapter_data_laporan.OnDeleteListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: adapter_data_laporan
    private val laporanList = ArrayList<ModelLaporan>()
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_laporan)

        database = FirebaseDatabase.getInstance().getReference("Laporan")

        recyclerView = findViewById(R.id.rvLaporanTransaksi)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pass listener ke adapter
        adapter = adapter_data_laporan(laporanList, this, this)
        recyclerView.adapter = adapter

        loadData()
    }

    private fun loadData() {
        database.orderByChild("tanggal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                laporanList.clear()
                for (dataSnapshot in snapshot.children) {
                    val laporan = dataSnapshot.getValue(ModelLaporan::class.java)
                    laporan?.let {
                        laporanList.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLaporanActivity, "Gagal memuat data laporan", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStatusChanged(position: Int, newStatus: StatusLaporan) {
        if (position < laporanList.size) {
            val laporan = laporanList[position]
            laporan.status = newStatus

            // Update di Firebase
            database.child(laporan.noTransaksi ?: "").child("status").setValue(newStatus)
                .addOnSuccessListener {
                    adapter.notifyItemChanged(position)
                    val statusMessage = when (newStatus) {
                        StatusLaporan.SUDAH_DIBAYAR -> this.getString(R.string.StatusberhasildiubahmenjadiSudahDibayar)
                        StatusLaporan.BELUM_DIBAYAR -> this.getString(R.string.StatusberhasildiubahmenjadiSudahDibayar)
                        StatusLaporan.SELESAI -> this.getString(R.string.Laporanberhasildihapus)
                    }
                    Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, this.getString(R.string.Gagalmengupdatestatus), Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDeleteItem(position: Int) {
        if (position < laporanList.size) {
            val laporan = laporanList[position]
            database.child(laporan.noTransaksi ?: "").removeValue()
                .addOnSuccessListener {
                    laporanList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this, this.getString(R.string.Laporanberhasildihapus), Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, this.getString(R.string.Gagalmenghapuslaporan), Toast.LENGTH_SHORT).show()
                }
        }
    }
}