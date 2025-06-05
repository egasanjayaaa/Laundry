// File: app/src/main/java/com/ega/laundry/transaksi/InvoiceActivity.kt
package com.ega.laundry.transaksi

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.modeldata.ModelTambahan
import com.ega.laundry.R
import kotlinx.coroutines.*
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InvoiceActivity : AppCompatActivity() {

    // UI elements for the invoice header
    private lateinit var tvBusinessName: TextView
    private lateinit var tvBranch: TextView

    // UI elements for transaction details
    private lateinit var tvTransactionId: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvCustomer: TextView
    private lateinit var tvEmployee: TextView

    // UI elements for the main service
    private lateinit var tvMainService: TextView
    private lateinit var tvMainServicePrice: TextView

    // UI elements for additional services
    private lateinit var tvAdditionalServicesHeader: TextView
    private lateinit var rvAdditionalServices: RecyclerView
    private lateinit var tvSubtotalAdditional: TextView

    // UI element for the total price
    private lateinit var tvTotal: TextView

    // UI elements for action buttons
    private lateinit var btnWhatsapp: Button
    private lateinit var btnPrint: Button

    // Data variables received from previous activity
    private var namaPelanggan: String = ""
    private var nomorHp: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = "0" // Main service price
    private var totalHarga: Int = 0 // Total calculated price
    private var metodePembayaran: String = "" // Payment method selected
    private var tambahanList: ArrayList<ModelTambahan> = ArrayList() // List of additional services

    // Generated transaction details
    private var noTransaksi: String = "" // Unique transaction ID
    private var tanggalTransaksi: String = "" // Date and time of transaction

    // Coroutine scope for managing background tasks like Bluetooth printing
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Companion object to hold constants, especially for Bluetooth
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1001
        private const val REQUEST_ENABLE_BT = 1002 // Although not directly used for prompting enable BT
        private const val PRINTER_MAC_ADDRESS = "DC:0D:51:A7:FF:7A" // Replace with your printer's MAC address
        private const val SPP_UUID = "00001101-0000-1000-8000-00805f9b34fb" // Standard Serial Port Profile UUID
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invoice) // Referensi ke layout invoice

        setupWindowInsets() // Set up edge-to-edge display
        initViews() // Initialize all UI elements
        extractIntentData() // Get data passed from previous activity
        setupInvoiceData() // Populate invoice with data
        setupClickListeners() // Set up button click actions
    }

    /**
     * Applies window insets to the main view to handle system bars (status and navigation bars).
     */
    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Initializes all UI elements by finding them by their IDs.
     */
    private fun initViews() {
        // Header
        tvBusinessName = findViewById(R.id.tv_business_name)
        tvBranch = findViewById(R.id.tv_branch)

        // Transaction Details
        tvTransactionId = findViewById(R.id.tv_transaction_id)
        tvDate = findViewById(R.id.tv_date)
        tvCustomer = findViewById(R.id.tv_customer)
        tvEmployee = findViewById(R.id.tv_employee)

        // Main Service
        tvMainService = findViewById(R.id.tv_main_service)
        tvMainServicePrice = findViewById(R.id.tv_main_service_price)

        // Additional Services Header and RecyclerView (Corrected ID for header)
        tvAdditionalServicesHeader = findViewById(R.id.tv_additional_services_header)
        rvAdditionalServices = findViewById(R.id.rv_additional_services) // Corrected ID from tvAdditionalServicesHeader
        tvSubtotalAdditional = findViewById(R.id.tv_subtotal_additional)

        // Total
        tvTotal = findViewById(R.id.tv_total)

        // Buttons
        btnWhatsapp = findViewById(R.id.btn_whatsapp)
        btnPrint = findViewById(R.id.btn_print)
    }

    /**
     * Extracts data passed from the previous activity (KonfirmasiDataTransaksi).
     */
    private fun extractIntentData() {
        namaPelanggan = intent.getStringExtra("nama_pelanggan") ?: ""
        nomorHp = intent.getStringExtra("nomor_hp") ?: ""
        namaLayanan = intent.getStringExtra("nama_layanan") ?: ""
        hargaLayanan = intent.getStringExtra("harga_layanan") ?: "0"
        totalHarga = intent.getIntExtra("total_harga", 0)
        metodePembayaran = intent.getStringExtra("metode_pembayaran") ?: ""

        val serializableExtra = intent.getSerializableExtra("layanan_tambahan")
        tambahanList = try {
            @Suppress("UNCHECKED_CAST")
            serializableExtra as? ArrayList<ModelTambahan> ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load additional services.", Toast.LENGTH_SHORT).show()
            ArrayList()
        }

        // Generate dynamic transaction ID and current date/time
        noTransaksi = generateNoTransaksi()
        tanggalTransaksi = getCurrentDateTime()
    }

    /**
     * Populates the invoice layout with the extracted and generated data.
     */
    @SuppressLint("SetTextI18n")
    private fun setupInvoiceData() {
        // Header info
        tvBusinessName.text = "Skena Laundry" // From strings.xml if preferred
        tvBranch.text = "Solo" // Dynamic from SharedPreferences or previous intent if multiple branches

        // Transaction details
        tvTransactionId.text = noTransaksi
        tvDate.text = tanggalTransaksi
        tvCustomer.text = namaPelanggan
        tvEmployee.text = "Admin" // This could be dynamic based on logged-in employee

        // Main service details
        tvMainService.text = namaLayanan
        tvMainServicePrice.text = formatCurrency(hargaLayanan.toIntOrNull() ?: 0)

        // Set up and display additional services
        setupAdditionalServices()

        // Display total price
        tvTotal.text = formatCurrency(totalHarga)
    }

    /**
     * Configures the RecyclerView for additional services. If no additional services, hides the section.
     */
    private fun setupAdditionalServices() {
        if (tambahanList.isEmpty()) {
            hideAdditionalServices() // Hide header and RecyclerView if list is empty
            tvSubtotalAdditional.text = formatCurrency(0) // Subtotal is 0 if no additional services
            return
        }

        // Show additional services section
        tvAdditionalServicesHeader.visibility = View.VISIBLE
        rvAdditionalServices.visibility = View.VISIBLE

        // Setup RecyclerView with a LinearLayoutManager and custom adapter
        rvAdditionalServices.layoutManager = LinearLayoutManager(this)
        val adapter = AdditionalServicesAdapter(tambahanList)
        rvAdditionalServices.adapter = adapter

        // Calculate and display subtotal for additional services
        val subtotal = tambahanList.sumOf {
            it.hargaTambahan?.toIntOrNull() ?: 0
        }
        tvSubtotalAdditional.text = formatCurrency(subtotal)
    }

    /**
     * Hides the additional services header and RecyclerView.
     */
    private fun hideAdditionalServices() {
        tvAdditionalServicesHeader.visibility = View.GONE
        rvAdditionalServices.visibility = View.GONE
    }

    /**
     * Sets up click listeners for the WhatsApp and Print buttons.
     */
    private fun setupClickListeners() {
        btnWhatsapp.setOnClickListener {
            shareToWhatsApp()
        }

        btnPrint.setOnClickListener {
            printReceipt()
        }
    }

    /**
     * Formats an integer amount into a currency string (Rupiah).
     * @param amount The integer amount to format.
     * @return Formatted currency string, e.g., "Rp10.000".
     */
    private fun formatCurrency(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount).replace("IDR", "Rp").replace(",00", "") // Remove ",00" if no cents are needed
    }

    /**
     * Composes a WhatsApp message with invoice details and opens WhatsApp.
     */
    private fun shareToWhatsApp() {
        val message = createWhatsAppMessage()
        val phoneNumber = formatPhoneNumber(nomorHp) // Ensure phone number is in international format
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        val url = "https://wa.me/$phoneNumber?text=$encodedMessage"

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            showToast("Gagal membuka WhatsApp. Pastikan aplikasi WhatsApp terinstal.")
            e.printStackTrace()
        }
    }

    /**
     * Generates the detailed message content for WhatsApp sharing.
     * @return Formatted string for WhatsApp.
     */
    private fun createWhatsAppMessage(): String {
        val additionalServicesText = if (tambahanList.isNotEmpty()) {
            tambahanList.joinToString("\n") { "- ${it.namaTambahan} (${formatCurrency(it.hargaTambahan?.toIntOrNull() ?: 0)})" }
        } else {
            "Tidak ada"
        }

        return """
            Halo $namaPelanggan 👋,

            🔖 *SKENA LAUNDRY - SOLO*

            🆔 *ID Transaksi:* $noTransaksi
            📅 *Tanggal:* $tanggalTransaksi
            👤 *Pelanggan:* $namaPelanggan

            ---
            🧺 *Layanan Utama:*
            $namaLayanan (${formatCurrency(hargaLayanan.toIntOrNull() ?: 0)})

            ✨ *Layanan Tambahan:*
            $additionalServicesText
            ---

            💰 *Total Bayar:* ${formatCurrency(totalHarga)}
            _Metode Pembayaran: $metodePembayaran

            🙏 Terima kasih telah mempercayakan cucian Anda kepada kami.
            Kami akan memberikan pelayanan terbaik untuk Anda!

            📍 Skena Laundry - Cabang Solo

        """.trimIndent()
    }

    /**
     * Formats a phone number to an international format (e.g., "0812..." to "62812...").
     * @param phoneNumber The original phone number string.
     * @return Formatted phone number.
     */
    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("0")) {
            "62${phoneNumber.substring(1)}"
        } else {
            phoneNumber
        }
    }

    /**
     * Generates a simple transaction ID based on current timestamp.
     * @return A string transaction ID.
     */
    private fun generateNoTransaksi(): String {
        val timestamp = System.currentTimeMillis()
        return "TRX${timestamp.toString().takeLast(8)}"
    }

    /**
     * Gets the current date and time formatted as "yyyy-MM-dd HH:mm:ss".
     * @return Formatted date and time string.
     */
    private fun getCurrentDateTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    // ==================== BLUETOOTH PRINTING LOGIC ====================

    /**
     * Initiates the receipt printing process by checking permissions and connecting to the printer.
     */
    private fun printReceipt() {
        if (checkBluetoothPermissions()) {
            connectAndPrint(PRINTER_MAC_ADDRESS)
        } else {
            requestBluetoothPermissions()
        }
    }

    /**
     * Checks if the app has the necessary Bluetooth permissions.
     * @return true if permissions are granted, false otherwise.
     */
    private fun checkBluetoothPermissions(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // For Android 12 (API 31) and above
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 11 (API 30) and below
            val bluetoothPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED

            val bluetoothAdminPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED

            bluetoothPermission && bluetoothAdminPermission // Both must be granted
        }
    }

    /**
     * Requests necessary Bluetooth permissions from the user.
     */
    private fun requestBluetoothPermissions() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN // BLUETOOTH_SCAN is often needed for discovering devices
            )
        } else {
            arrayOf(
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                android.Manifest.permission.ACCESS_FINE_LOCATION // Needed for Bluetooth scanning on older APIs
            )
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
    }

    /**
     * Attempts to connect to a Bluetooth device and print the receipt.
     * This operation is performed on a background thread using coroutines.
     * @param macAddress The MAC address of the Bluetooth printer.
     */
    private fun connectAndPrint(macAddress: String) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            showToast("Bluetooth tidak didukung pada perangkat ini")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            showToast("Bluetooth tidak aktif. Silakan aktifkan Bluetooth.")
            // Optionally, you can prompt the user to enable Bluetooth:
            // val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            return
        }

        if (!checkBluetoothPermissions()) {
            showToast("Izin Bluetooth tidak diberikan. Tidak dapat mencetak.")
            return
        }

        // Launch a coroutine for background printing
        coroutineScope.launch {
            try {
                // Perform the printing operation on an IO dispatcher thread
                val result = withContext(Dispatchers.IO) {
                    printToBluetoothDevice(bluetoothAdapter, macAddress)
                }

                // Update UI on the main thread based on the result
                if (result) {
                    showToast("Struk berhasil dicetak!")
                } else {
                    showToast("Gagal mencetak struk. Pastikan printer terhubung dan dalam jangkauan.")
                }
            } catch (e: Exception) {
                showToast("Terjadi kesalahan saat mencetak: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Performs the actual Bluetooth connection and printing.
     * This function should be called from a background thread (e.g., Dispatchers.IO).
     * @param bluetoothAdapter The BluetoothAdapter instance.
     * @param macAddress The MAC address of the printer.
     * @return true if printing was successful, false otherwise.
     */
    private suspend fun printToBluetoothDevice(
        bluetoothAdapter: BluetoothAdapter,
        macAddress: String
    ): Boolean {
        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null

        return try {
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)
            val uuid = UUID.fromString(SPP_UUID) // SPP (Serial Port Profile) UUID

            // Create a RFCOMM (Bluetooth) socket and connect to the device
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect() // This is a blocking call, must be on a background thread

            outputStream = socket.outputStream // Get the output stream to send data
            val receiptData = generateReceiptData() // Generate the receipt content
            outputStream.write(receiptData.toByteArray()) // Write data to the printer
            outputStream.flush() // Flush any buffered data

            true // Printing successful
        } catch (e: IOException) {
            e.printStackTrace()
            false // IO error during connection or printing
        } catch (e: SecurityException) {
            e.printStackTrace()
            false // Security permission issue
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false // Invalid MAC address or UUID
        } finally {
            // Ensure streams and sockets are closed to release resources
            try {
                outputStream?.close()
                socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Generates the receipt content as a string, including ESC/POS commands for formatting.
     * @return The formatted receipt string.
     */
    private fun generateReceiptData(): String {
        val receipt = StringBuilder()

        // ESC/POS Commands (common commands for thermal printers)
        val ESC = "\u001B" // Escape character
        val INIT = "$ESC@" // Initialize printer
        val CENTER = "${ESC}a\u0001" // Align text to center
        val LEFT = "${ESC}a\u0000" // Align text to left
        val BOLD_ON = "${ESC}E\u0001" // Turn on bold mode
        val BOLD_OFF = "${ESC}E\u0000" // Turn off bold mode
        val CUT = "${ESC}i" // Full cut paper (might vary per printer model)

        receipt.apply {
            append(INIT) // Initialize printer
            append(CENTER)
            append(BOLD_ON)
            append("SKENA LAUNDRY\n")
            append("SOLO\n")
            append(BOLD_OFF)
            append("================================\n")
            append(LEFT)
            append("\n")

            // Transaction Details Section
            append("ID Transaksi: $noTransaksi\n")
            append("Tanggal: $tanggalTransaksi\n")
            append("Pelanggan: $namaPelanggan\n")
            append("Kasir: Admin\n") // Dynamic if employee data is available
            append("--------------------------------\n")

            // Main Service Section
            append(BOLD_ON)
            append("LAYANAN UTAMA:\n")
            append(BOLD_OFF)
            append("$namaLayanan\n")
            append("${formatCurrency(hargaLayanan.toIntOrNull() ?: 0)}\n")
            append("--------------------------------\n")

            // Additional Services Section (only if available)
            if (tambahanList.isNotEmpty()) {
                append(BOLD_ON)
                append("LAYANAN TAMBAHAN:\n")
                append(BOLD_OFF)
                tambahanList.forEachIndexed { index, tambahan ->
                    val harga = tambahan.hargaTambahan?.toIntOrNull() ?: 0
                    append("${index + 1}. ${tambahan.namaTambahan}\n")
                    append("   ${formatCurrency(harga)}\n")
                }
                append("--------------------------------\n")
            }

            // Total Section
            append(BOLD_ON)
            append("TOTAL: ${formatCurrency(totalHarga)}\n")
            append(BOLD_OFF)
            append("================================\n")
            append(CENTER) // Center alignment for footer
            append("\n")
            append("Terima kasih atas kepercayaan\n")
            append("Anda kepada kami!\n")
            append("\n")
            append("================================\n")
            append("\n\n\n") // Extra newlines for paper feed before cut
            append(CUT) // Cut paper command
        }

        return receipt.toString()
    }

    /**
     * Displays a short Toast message.
     * @param message The string message to display.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Callback for permission request results.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                // Check if all requested permissions were granted
                if (grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    printReceipt() // If granted, proceed with printing
                } else {
                    showToast("Izin Bluetooth diperlukan untuk mencetak struk.")
                }
            }
        }
    }

    /**
     * Called when the activity is being destroyed. Cancels any running coroutines.
     */
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel all coroutines launched in this scope
    }

    /**
     * Inner class for RecyclerView Adapter to display additional services on the invoice.
     */
    inner class AdditionalServicesAdapter(
        private val additionalServices: List<ModelTambahan>
    ) : RecyclerView.Adapter<AdditionalServicesAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvNumber: TextView = itemView.findViewById(R.id.tv_number)
            val tvServiceName: TextView = itemView.findViewById(R.id.tv_service_name)
            val tvServicePrice: TextView = itemView.findViewById(R.id.tv_service_price)
            // tv_service_description and tv_quantity are in layout but not used in adapter currently
            // val tvServiceDescription: TextView = itemView.findViewById(R.id.tv_service_description)
            // val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_additional_service, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val service = additionalServices[position]
            holder.tvNumber.text = (position + 1).toString() // Display item number
            holder.tvServiceName.text = service.namaTambahan ?: "Layanan Tambahan" // Display service name
            holder.tvServicePrice.text = formatCurrency(service.hargaTambahan?.toIntOrNull() ?: 0) // Format and display price
        }

        override fun getItemCount(): Int = additionalServices.size // Return the total number of items
    }
}