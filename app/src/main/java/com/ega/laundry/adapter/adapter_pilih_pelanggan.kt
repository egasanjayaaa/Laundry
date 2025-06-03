package com.ega.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelPelanggan
import com.ega.laundry.transaksi.DataTransaksiActivity

class adapter_pilih_pelanggan(private val listPelanggan: ArrayList<ModelPelanggan>) :
    RecyclerView.Adapter<adapter_pilih_pelanggan.ViewHolder>() {

    private val TAG = "adapter_pilih_pelanggan"
    lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_pilih_pelanggan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = listPelanggan[position]

        try {
            holder.tvID.text = "[$nomor]"
            holder.tvNama.text = item.namaPelanggan ?: "Nama tidak tersedia"
            holder.tvNoHP.text = "No HP : ${item.noHPPelanggan ?: "Tidak tersedia"}"

            holder.cvCARD.setOnClickListener {
                try {
                    val intent = Intent(appContext, DataTransaksiActivity::class.java)
                    intent.putExtra("idPelanggan", item.idPelanggan)
                    intent.putExtra("nama", item.namaPelanggan)
                    intent.putExtra("nohp", item.noHPPelanggan)
                    (appContext as Activity).setResult(Activity.RESULT_OK, intent)
                    (appContext as Activity).finish()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in click listener: ${e.message}")
                    Toast.makeText(appContext, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error binding view holder: ${e.message}")
        }
    }

    override fun getItemCount(): Int {
        val count = listPelanggan.size
        Log.d(TAG, "getItemCount: $count")
        return count
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_pilih_pelanggan_nama)
        val tvNoHP: TextView = itemView.findViewById(R.id.tvCARD_pilih_pelanggan_nohp)
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIH_PELANGGAN)
    }
}
