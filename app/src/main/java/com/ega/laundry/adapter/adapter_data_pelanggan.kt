package com.ega.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelPelanggan

class adapter_data_pelanggan(
   private val listPelanggan: ArrayList<ModelPelanggan>
) : RecyclerView.Adapter<adapter_data_pelanggan.ViewHolder>() {

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val view = LayoutInflater.from(parent.context)
         .inflate(R.layout.card_data_pelanggan, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val pelanggan = listPelanggan[position]
      holder.tvCARD_PELANGGAN_ID.text = pelanggan.idPelanggan
      holder.tvCARD_PELANGGAN_NAMA.text = pelanggan.namaPelanggan
      holder.tvCARD_PELANGGAN_ALAMAT.text = pelanggan.alamatPelanggan
      holder.tvCARD_PELANGGAN_NOHP.text = pelanggan.noHPPelanggan

      // Klik CardView
      holder.cvCARD_PELANGGAN.setOnClickListener {
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
   }

   override fun getItemCount(): Int {
      return listPelanggan.size
   }

   class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val tvCARD_PELANGGAN_ID: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_ID)
      val tvCARD_PELANGGAN_NAMA: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nama)
      val tvCARD_PELANGGAN_ALAMAT: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_alamat)
      val tvCARD_PELANGGAN_NOHP: TextView = itemView.findViewById(R.id.tvCARD_PELANGGAN_nohp)
      val cvCARD_PELANGGAN: CardView = itemView.findViewById(R.id.cvCARD_PELANGGAN)
      val btnHubungi: Button = itemView.findViewById(R.id.btn_hubungi)
      val btnLihat: Button = itemView.findViewById(R.id.btn_lihat)
   }
}
