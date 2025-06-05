// File: app/src/main/java/com/ega/laundry/transaksi/PilihLayananActivity.kt
package com.ega.laundry.transaksi

import android.annotation.SuppressLint
import android.app.Activity // Needed for Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText // Changed to EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
// import androidx.appcompat.widget.SearchView // No longer needed if using EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.adapter.adapter_pilih_layanan
import com.ega.laundry.modeldata.ModelLayanan
import com.google.firebase.database.*

class PilihLayananActivity : AppCompatActivity() {

    private val TAG = "PilihLayanan" // Tag for logging
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan") // Reference to "layanan" node in Firebase

    // UI elements
    private lateinit var rvPilihLayanan: RecyclerView
    private lateinit var etSearch: EditText // Changed type and name to etSearch
    private lateinit var tvKosong: TextView

    // Data lists
    private lateinit var listLayanan: ArrayList<ModelLayanan> // Original list from Firebase
    private lateinit var filteredList: ArrayList<ModelLayanan> // List shown in RecyclerView after filtering
    private lateinit var adapter: adapter_pilih_layanan // Adapter for the RecyclerView

    private var idCabang: String = "" // To filter services by branch

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pilih_layanan) // Referensi ke layout pemilihan layanan

        Log.d(TAG, "Activity created")

        initViews() // Initialize UI elements

        listLayanan = ArrayList()
        filteredList = ArrayList()

        // Set up RecyclerView
        rvPilihLayanan.layoutManager = LinearLayoutManager(this)
        adapter = adapter_pilih_layanan(filteredList) // Initialize adapter with filtered list
        rvPilihLayanan.adapter = adapter

        // Get idCabang from intent (passed from DataTransaksiActivity)
        idCabang = intent.getStringExtra("idCabang") ?: ""
        Log.d(TAG, "idCabang received: $idCabang")

        setupSearchInput() // Changed function call to setupSearchInput for EditText
        getData() // Fetch data from Firebase

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Initializes UI elements.
     */
    private fun initViews() {
        tvKosong = findViewById(R.id.tvKosong)
        rvPilihLayanan = findViewById(R.id.rvPILIH_LAYANAN)
        etSearch = findViewById(R.id.etSearch) // Corrected ID to etSearch
    }

    /**
     * Sets up the EditText for text change listeners.
     */
    private fun setupSearchInput() { // Renamed from setupSearchView
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not used in this implementation
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used in this implementation
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString()) // Filter as text changes
            }
        })
    }

    /**
     * Filters the [listLayanan] based on the provided [query] and updates [filteredList].
     * @param query The text to filter by.
     */
    private fun filterList(query: String?) {
        Log.d(TAG, "Filtering list with query: $query")
        filteredList.clear() // Clear previous filtered results

        if (query.isNullOrEmpty()) {
            filteredList.addAll(listLayanan) // If query is empty, show all services
        } else {
            val searchText = query.lowercase().trim()
            for (layanan in listLayanan) {
                // Filter by service name or price (case-insensitive)
                if (layanan.namalayanan?.lowercase()?.contains(searchText) == true ||
                    layanan.hargalayanan?.lowercase()?.contains(searchText) == true
                ) {
                    filteredList.add(layanan)
                }
            }
        }
        updateRecyclerView() // Update the RecyclerView with filtered data
    }

    /**
     * Updates the RecyclerView adapter with the current [filteredList] and manages the empty state text.
     */
    private fun updateRecyclerView() {
        Log.d(TAG, "Updating RecyclerView with ${filteredList.size} items")

        if (filteredList.isEmpty()) {
            tvKosong.visibility = View.VISIBLE
            tvKosong.text = "Tidak ada layanan yang cocok" // Message for no matching results
        } else {
            tvKosong.visibility = View.GONE
        }
        // Notify the adapter that the dataset has changed
        adapter.notifyDataSetChanged()
    }

    /**
     * Fetches service data from Firebase Realtime Database.
     * Filters by 'idCabang' if provided, otherwise fetches all services.
     */
    private fun getData() {
        Log.d(TAG, "getData() called")

        val query: Query = if (idCabang.isEmpty()) {
            // If no branch ID, fetch all services (e.g., for super admin)
            myRef.limitToLast(100) // Limit to last 100 for performance
        } else {
            // Fetch services for a specific branch
            myRef.orderByChild("cabanglayanan").equalTo(idCabang).limitToLast(100) // Filter by branch name/ID
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: data exists: ${snapshot.exists()}, count: ${snapshot.childrenCount}")

                listLayanan.clear() // Clear previous data before adding new
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        try {
                            val layanan = snap.getValue(ModelLayanan::class.java)
                            layanan?.let {
                                listLayanan.add(it)
                                Log.d(TAG, "Added layanan: ${it.namalayanan} (ID: ${it.idlayanan})")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing layanan: ${e.message}", e)
                            Toast.makeText(this@PilihLayananActivity, "Error parsing data.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    filteredList.clear()
                    filteredList.addAll(listLayanan) // Initially, filtered list is all data
                    updateRecyclerView() // Update UI
                } else {
                    Log.d(TAG, "No data found for the given criteria.")
                    tvKosong.visibility = View.VISIBLE
                    tvKosong.text = "Data layanan tidak ditemukan." // Specific message for no data
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}", error.toException())
                tvKosong.visibility = View.VISIBLE
                tvKosong.text = "Error memuat data: ${error.message}"
                Toast.makeText(this@PilihLayananActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}