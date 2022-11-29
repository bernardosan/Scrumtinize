package com.example.trellocloneapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.activities.MainActivity
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemBoardBinding
import com.example.trellocloneapp.models.Board

class MainAdapter(val boardList: ArrayList<Board>, val context: Context):RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener ? = null

    inner class MainViewHolder (val itemBinding: ItemBoardBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(board: Board){
            itemBinding.tvBoardName.text = board.name
            itemBinding.tvCreateBy.text =  "created by: " + board.createdBy
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

    interface  OnLongClickListener{
        fun onLongClick(position: Int, model: Board){
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val board = boardList[position]
        holder.bindItem(board)
        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                onClickListener!!.onClick(position, board)
            }
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener!!.onLongClick(position, board)
            true
        }


    }

    override fun getItemCount(): Int {
        return boardList.size

    }


}