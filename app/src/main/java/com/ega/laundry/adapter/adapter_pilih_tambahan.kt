package com.ega.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ega.laundry.R
import com.ega.laundry.modeldata.ModelTambahan

class adapter_pilih_tambahan(
    private val listTambahan: MutableList<ModelTambahan>,
    private val enableLongDelete: Boolean = false, // true hanya di DataTransaksi
    private val onDelete: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<adapter_pilih_tambahan.ViewHolder>() {

    private val visibleDeleteSet = mutableSetOf<Int>()
    private lateinit var appContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        appContext = parent.context
        val view = LayoutInflater.from(appContext).inflate(R.layout.card_pilih_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nomor = position + 1
        val item = listTambahan[position]

        holder.tvID.text = "[$nomor]"
        holder.tvNama.text = item.namaTambahan ?: "Tidak ada nama"
        holder.tvHarga.text = "Harga: Rp ${item.hargaTambahan ?: "0"}"

        // Tampilkan atau sembunyikan tombol delete
        holder.btnDelete.visibility = if (visibleDeleteSet.contains(position)) View.VISIBLE else View.GONE

        // Long click untuk tampilkan tombol delete
        if (enableLongDelete) {
            holder.cvCARD.setOnLongClickListener {
                visibleDeleteSet.add(position)
                notifyItemChanged(position)
                true
            }
        } else {
            // Klik biasa untuk memilih data tambahan
            holder.cvCARD.setOnClickListener {
                val intent = Intent().apply {
                    putExtra("idTambahan", item.idTambahan)
                    putExtra("namaTambahan", item.namaTambahan)
                    putExtra("hargaTambahan", item.hargaTambahan)
                }
                (appContext as? Activity)?.setResult(Activity.RESULT_OK, intent)
                (appContext as? Activity)?.finish()
            }
        }

        // Klik tombol hapus
        holder.btnDelete.setOnClickListener {
            onDelete?.invoke(position)
            visibleDeleteSet.remove(position)
            notifyItemChanged(position) // update tampilan
        }
    }

    override fun getItemCount(): Int = listTambahan.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_ID)
        val tvNama: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_nama)
        val tvHarga: TextView = itemView.findViewById(R.id.tvCARD_PILIHTAMBAHAN_harga)
        val cvCARD: CardView = itemView.findViewById(R.id.cvCARD_PILIHTAMBAHAN)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }
}
