// app/src/main/java/com/ega/laundry/cabang/DataCabangActivity.kt
package com.ega.laundry.cabang

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.adapter.adapter_data_cabang
import com.ega.laundry.modeldata.ModelCabang
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataCabangActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("cabang") // Reference to your "cabang" node
    lateinit var rvDATA_CABANG: RecyclerView
    lateinit var fabDATA_CABANG_Tambah: FloatingActionButton
    lateinit var cabangList: ArrayList<ModelCabang>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_cabang) // Ensure you create this layout file

        initViews()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Display latest added at the top
        layoutManager.stackFromEnd = true // For smoother reverse layout behavior
        rvDATA_CABANG.layoutManager = layoutManager
        rvDATA_CABANG.setHasFixedSize(true)

        cabangList = arrayListOf<ModelCabang>()
        getData() // Fetch data from Firebase

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fabDATA_CABANG_Tambah.setOnClickListener {
            val intent = Intent(this, TambahCabangActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        rvDATA_CABANG = findViewById(R.id.rvDATA_CABANG)
        fabDATA_CABANG_Tambah = findViewById(R.id.fabDATA_PENGGUNA_TambahCabang)
    }

    private fun getData() {
        val query = myRef.orderByChild("idCabang").limitToLast(100) // Order by ID, limit to last 100 for efficiency
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    cabangList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val cabang = dataSnapshot.getValue(ModelCabang::class.java)
                        if (cabang != null) {
                            cabangList.add(cabang)
                        }
                    }
                    val adapter = adapter_data_cabang(cabangList) { selectedCabang ->
                        // Handle long click (e.g., show edit/delete dialog)
                        showEditDeleteDialog(selectedCabang)
                    }
                    rvDATA_CABANG.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@DataCabangActivity, "Tidak ada data cabang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataCabangActivity, "Data Gagal Dimuat: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditDeleteDialog(cabang: ModelCabang) {
        val options = arrayOf("Edit", "Hapus")
        AlertDialog.Builder(this)
            .setTitle("Pilih Aksi")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> { // Edit
                        val intent = Intent(this, TambahCabangActivity::class.java)
                        intent.putExtra("idCabang", cabang.idCabang)
                        intent.putExtra("namaCabang", cabang.namaCabang)
                        intent.putExtra("alamatCabang", cabang.alamatCabang)
                        intent.putExtra("noHPCabang", cabang.noHPCabang)
                        startActivity(intent)
                    }
                    1 -> { // Delete
                        AlertDialog.Builder(this)
                            .setTitle("Konfirmasi Hapus")
                            .setMessage("Apakah Anda yakin ingin menghapus cabang '${cabang.namaCabang}'?")
                            .setPositiveButton("Hapus") { _, _ ->
                                if (cabang.idCabang != null) {
                                    myRef.child(cabang.idCabang).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Cabang berhasil dihapus", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Gagal menghapus cabang: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                }
            }
            .show()
    }
}