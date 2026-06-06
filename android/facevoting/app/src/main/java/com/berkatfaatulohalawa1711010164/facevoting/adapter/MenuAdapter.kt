package com.berkatfaatulohalawa1711010164.facevoting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berkatfaatulohalawa1711010164.facevoting.R
import com.berkatfaatulohalawa1711010164.facevoting.config.Constants
import com.berkatfaatulohalawa1711010164.facevoting.model.MenuModel
import com.berkatfaatulohalawa1711010164.facevoting.ui.Paslon
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class MenuAdapter(
    private val context: Context,
    private var mMenuList: List<MenuModel>
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.single_row_kategori, parent, false)
        return MyViewHolder(mView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context)
            .load(Constants.IMAGES_URL + mMenuList[position].logo_kategori)
            .apply(RequestOptions().error(R.drawable.logosmall))
            .into(MyViewHolder.mlogoukm)
        holder.mTextJudul.text = mMenuList[position].nama_kategori
        holder.mTombolVote.setOnClickListener { v ->
            val mIntent = Intent(v.context, Paslon::class.java)
            mIntent.putExtra("id_kategori", mMenuList[position].id_kategori)
            v.context.startActivity(mIntent)
        }
    }

    override fun getItemCount(): Int = mMenuList.size

    fun replaceData(daftarMenu: List<MenuModel>) {
        mMenuList = daftarMenu
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTextJudul: TextView = itemView.findViewById(R.id.txtJudul)
        val mTombolVote: Button = itemView.findViewById(R.id.tbl_vote)

        companion object {
            @SuppressLint("StaticFieldLeak")
            lateinit var mlogoukm: ImageView
        }

        init {
            mlogoukm = itemView.findViewById(R.id.logo_ukm)
        }
    }
}
