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
import com.ega.laundry.modeldata.ModelPegawai
import com.google.firebase.database.DatabaseReference
import com.ega.laundry.pegawai.TambahPegawaiActivity

class adapter_data_pegawai(
    private val listPegawai: ArrayList<ModelPegawai>
) : RecyclerView.Adapter<adapter_data_pegawai.ViewHolder>() {

    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pegawai = listPegawai[position]
        holder.tvCARD_PEGAWAI_ID.text = pegawai.idPegawai
        holder.tvCARD_PEGAWAI_NAMA.text = pegawai.namaPegawai
        holder.tvCARD_PEGAWAI_ALAMAT.text = pegawai.alamatPegawai
        holder.tvCARD_PEGAWAI_NOHP.text = pegawai.noHPPegawai

        // Klik CardView
        holder.cvCARD_PEGAWAI.setOnClickListener {
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

        holder.cvCARD_PEGAWAI.setOnClickListener{
            val intent = Intent (appContext, TambahPegawaiActivity::class.java)
            intent.putExtra("judul", "Edit Pelanggan")
            intent.putExtra("idPelanggan", "idPelanggan")
            intent.putExtra("alamatPelanggan", "alamatPelanggan")
            intent.putExtra("namapelanggan", "namaPelanggan")
            intent.putExtra("noHpPelanggan", "noHpPelanggan")
        }
    }

    override fun getItemCount(): Int {
        return listPegawai.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCARD_PEGAWAI_ID: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_ID)
        val tvCARD_PEGAWAI_NAMA: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_nama)
        val tvCARD_PEGAWAI_ALAMAT: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_alamat)
        val tvCARD_PEGAWAI_NOHP: TextView = itemView.findViewById(R.id.tvCARD_PEGAWAI_nohp)
        val cvCARD_PEGAWAI: CardView = itemView.findViewById(R.id.cvCARD_PEGAWAI)
        val btnHubungi: Button = itemView.findViewById(R.id.btn_hubungi)
        val btnLihat: Button = itemView.findViewById(R.id.btn_lihat)
    }
}
