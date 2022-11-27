package com.example.trellocloneapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.RecyclerviewItemBinding
import com.example.trellocloneapp.models.Board

class MainAdapter(val boardList: ArrayList<Board>, val context: Context):RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (val itemBinding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(board: Board){
            itemBinding.tvBoardName.text = board.name
            itemBinding.tvCreateBy.text = board.createdBy
            Glide
                .with(context)
                .load(board.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(itemBinding.ivBoardImage)
        }

    }

    interface  OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val board = boardList[position]
        holder.bindItem(board)
        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position, board)
            }
        }
    }

    override fun getItemCount(): Int {
        return boardList.size

    }
}