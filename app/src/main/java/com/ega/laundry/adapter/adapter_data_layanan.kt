package com.ega.laundry.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelLayanan
import com.ega.laundry.layanan.TambahLayananActivity
import com.google.firebase.database.DatabaseReference

class adapter_data_layanan(
    private val listlayanan: ArrayList<ModelLayanan>
) : RecyclerView.Adapter<adapter_data_layanan.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_layanan, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layanan = listlayanan[position]
        holder.tvCARD_LAYANAN_ID.text = layanan.idlayanan
        holder.tvCARD_LAYANAN_NAMA.text = layanan.namalayanan
        holder.tvCARD_LAYANAN_HARGA.text = layanan.hargalayanan
        holder.tvCARD_LAYANAN_CABANG.text = layanan.cabanglayanan

        // Klik CardView
        holder.cvCARD_LAYANAN.setOnClickListener {
            // Tambahkan aksi ketika card diklik
        }

        // Klik tombol hubungi
        holder.btnHubungi.setOnClickListener {
            // Tambahkan aksi ketika tombol "Hubungi" diklik
        }

        // Klik tombol lihat
        holder.btnLihat.setOnClickListener {
            // Tambahkan aksi ketika tombol "Lihat" diklik
        }

        holder.cvCARD_LAYANAN.setOnClickListener{
            val intent = Intent (appContext, TambahLayananActivity::class.java)
            intent.putExtra("judul", "Edit Pelanggan")
            intent.putExtra("idlayanan", "idlayanan")
            intent.putExtra("namalayanan", "namalayanan")
            intent.putExtra("hargalayanan", "hargalayanan")
            intent.putExtra("cabanglayanan", "cabanglayanan")
        }
    }

    override fun getItemCount(): Int {
        return listlayanan.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCARD_LAYANAN_ID: TextView = itemView.findViewById(R.id.tvCARD_LAYANAN_ID)
        val tvCARD_LAYANAN_NAMA: TextView = itemView.findViewById(R.id.tvCARD_LAYANAN_nama)
        val tvCARD_LAYANAN_HARGA: TextView = itemView.findViewById(R.id.tvCARD_LAYANAN_harga)
        val tvCARD_LAYANAN_CABANG: TextView = itemView.findViewById(R.id.tvCARD_LAYANAN_cabang)
        val cvCARD_LAYANAN: CardView = itemView.findViewById(R.id.cvCARD_LAYANAN)
        val btnHubungi: Button = itemView.findViewById(R.id.btn_hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btn_lihat)
    }
}