// File: app/src/main/java/com/ega/laundry/transaksi/KonfirmasiDataTransaksi.kt
package com.ega.laundry.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelTambahan
import java.io.Serializable // Important for passing ArrayList of custom objects

class KonfirmasiDataTransaksi : AppCompatActivity() {

    // Declare UI elements
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHP: TextView
    private lateinit var tvNamaLayanan: TextView
    private lateinit var tvHargaLayanan: TextView
    private lateinit var listLayananTambahan: ListView // Using ListView for simplicity as in original
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnBatal: Button
    private lateinit var btnPembayaran: Button

    // Data variables to hold transaction details
    private var namaPelanggan: String = ""
    private var nomorHp: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = "0" // Stored as String, converted to Int for calculation
    private var tambahanList = ArrayList<ModelTambahan>()
    private var totalHarga: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konfirmasi_data_transaksi) // Referensi ke layout konfirmasi transaksi

        initViews() // Initialize all UI elements

        // Retrieve data from the incoming Intent
        extractIntentData()

        // Populate UI with extracted data
        populateViews()

        // Calculate the total price of the transaction
        calculateTotalPrice()

        // Set up click listeners for buttons
        setupClickListeners()
    }

    /**
     * Initializes all TextViews, ListView, and Buttons from the layout.
     */
    private fun initViews() {
        tvNamaPelanggan = findViewById(R.id.tvNamaPelangganKonfirmasi)
        tvNoHP = findViewById(R.id.tvNoHPKonfimasi)
        tvNamaLayanan = findViewById(R.id.tvNamaLayananKonfimasi)
        tvHargaLayanan = findViewById(R.id.tvHargaLayananKonfimasi)
        listLayananTambahan = findViewById(R.id.listLayananTambahanKonfimasi)
        tvTotalBayar = findViewById(R.id.tvTotalHarga)
        btnBatal = findViewById(R.id.btnBatal)
        btnPembayaran = findViewById(R.id.btnPembayaran)
    }

    /**
     * Extracts all necessary transaction data from the Intent.
     */
    private fun extractIntentData() {
        namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        hargaLayanan = intent.getStringExtra("harga_layanan") ?: "0"

        // Safely retrieve the ArrayList of ModelTambahan
        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")
        @Suppress("UNCHECKED_CAST")
        tambahanList = try {
            serializableExtra as? ArrayList<ModelTambahan> ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error retrieving additional services data.", Toast.LENGTH_SHORT).show()
            ArrayList()
        }
    }

    /**
     * Populates the TextViews and ListView with the extracted transaction data.
     */
    @SuppressLint("SetTextI18n")
    private fun populateViews() {
        tvNamaPelanggan.text = namaPelanggan
        tvNoHP.text = nomorHp
        tvNamaLayanan.text = namaLayanan
        tvHargaLayanan.text = "Rp ${hargaLayanan.replace(".0", "")}" // Format main service price

        // Prepare strings for ListView adapter
        val tambahanStrings = ArrayList<String>()
        for (tambahan in tambahanList) {
            val nama = tambahan.namaTambahan ?: "Unknown"
            val harga = tambahan.hargaTambahan ?: "0"
            tambahanStrings.add("$nama - Rp${harga.replace(".0", "")}") // Format additional service price
        }

        // Set the adapter for ListView to display additional services
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, // Simple built-in layout
            tambahanStrings
        )
        listLayananTambahan.adapter = adapter
    }

    /**
     * Calculates the total price including main service and all additional services.
     */
    @SuppressLint("SetTextI18n")
    private fun calculateTotalPrice() {
        totalHarga = hargaLayanan.toIntOrNull() ?: 0 // Start with main service price
        for (tambahan in tambahanList) {
            val hargaTambahan = tambahan.hargaTambahan?.toIntOrNull() ?: 0
            totalHarga += hargaTambahan
        }
        tvTotalBayar.text = "Rp $totalHarga" // Display total price
    }

    /**
     * Sets up click listeners for the "Batal" and "Pembayaran" buttons.
     */
    private fun setupClickListeners() {
        btnBatal.setOnClickListener {
            finish() // Close this activity and return to the previous one
        }

        btnPembayaran.setOnClickListener {
            showPaymentMethodDialog() // Show the payment method selection dialog
        }
    }

    /**
     * Displays a dialog for selecting the payment method.
     */
    private fun showPaymentMethodDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialogmod_pembayaran, null)

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true) // Allow dismissing the dialog by tapping outside or back button
            .create()

        // List of payment methods and their corresponding CardView IDs
        val metodeList = listOf(
            Pair("Bayar Nanti", R.id.cardBayarNanti),
            Pair("Tunai", R.id.cardTunai),
            Pair("QRIS", R.id.cardQRIS),
            Pair("DANA", R.id.cardDANA),
            Pair("GoPay", R.id.cardGoPay),
            Pair("OVO", R.id.cardOVO)
        )

        // Set click listeners for each payment method card
        for ((namaMetode, idCard) in metodeList) {
            val card = dialogView.findViewById<androidx.cardview.widget.CardView>(idCard)
            card?.setOnClickListener {
                Toast.makeText(this, "Metode dipilih: $namaMetode", Toast.LENGTH_SHORT).show()

                // Navigate to InvoiceActivity, passing all transaction details
                val invoiceIntent = Intent(this, InvoiceActivity::class.java).apply {
                    putExtra("nama_pelanggan", namaPelanggan)
                    putExtra("nomor_hp", nomorHp)
                    putExtra("nama_layanan", namaLayanan)
                    putExtra("harga_layanan", hargaLayanan)
                    putExtra("total_harga", totalHarga)
                    putExtra("metode_pembayaran", namaMetode)
                    // Pass the list of additional services. ModelTambahan must be Serializable.
                    putExtra("layanan_tambahan", tambahanList as Serializable)
                }

                startActivity(invoiceIntent)
                dialog.dismiss() // Dismiss payment dialog
                finish() // Close KonfirmasiDataTransaksi activity as transaction flow continues
            }
        }

        // Set click listener for the "Batal" (Cancel) button in the dialog
        val btnBatalDialog = dialogView.findViewById<TextView>(R.id.tvBatal)
        btnBatalDialog?.setOnClickListener {
            dialog.dismiss() // Dismiss the payment dialog
        }

        dialog.show() // Show the payment method selection dialog
    }
}