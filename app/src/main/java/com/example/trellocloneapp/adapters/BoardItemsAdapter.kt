package com.example.trellocloneapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById(R.id.iv_my_profile))

            holder.itemView.findViewById<TextView>(R.id.tv_board_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_create_by). text = "Created by: " + FirestoreClass().getUserNameById(model.createdBy)
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.OnClick(position, model)
                }
            }
        }
    }

    interface  OnClickListener{
        fun OnClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

}