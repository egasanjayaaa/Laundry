package com.ega.adapter
import com.ega.laundry.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.modeldata.ModelPelanggan

class adapter_data_pelanggan(
   private val listPelanggan: ArrayList<ModelPelanggan>) :
   RecyclerView.Adapter<adapter_data_pelanggan.ViewHolder>() {

   override fun onCreateViewHolder(
      parent: ViewGroup,
      viewType: Int
   ): ViewHolder {
      val view = LayoutInflater.from(parent.context)
         .inflate(R.layout.card_data_pelanggan, parent, false)
      return ViewHolder(view)
   }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val pelanggan = listPelanggan[position]
   }

   override fun getItemCount(): Int {
      return listPelanggan.size
   }

   class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      val cvCARD_PELANGGAN = itemView.findViewById<View>(R.id.cvCARD_PELANGGAN)
      val tvCARD_PELANGGAN_ID = itemView.findViewById<View>(R.id.tvCARD_PELANGGAN_ID)
      val tvCARD_PELANGGAN_NAMA = itemView.findViewById<View>(R.id.tvCARD_PELANGGAN_nama)
      val tvCARD_PELANGGAN_ALAMAT = itemView.findViewById<View>(R.id.tvCARD_PELANGGAN_alamat)
      val tvCARD_PELANGGAN_NOHP = itemView.findViewById<View>(R.id.tvCARD_PELANGGAN_nohp)

   }
}