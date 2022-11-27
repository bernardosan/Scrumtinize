package com.example.trellocloneapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.RecyclerviewItemBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board

class BoardItemsAdapter(private var list: ArrayList<Board>): RecyclerView.Adapter<BoardItemsAdapter.MainViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(itemBinding.root){

        @SuppressLint("SetTextI18n")
        fun bindItem(model: Board){
            itemBinding.tvBoardName.text = model.name
            itemBinding.tvCreateBy.text = "Created by: " + FirestoreClass().getUserNameById(model.createdBy)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface  OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val model = list[position]

        holder.bindItem(model)

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position, model)
            }
        }

    }

}