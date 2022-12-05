package com.example.trellocloneapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.databinding.ItemLabelColorBinding

class LabelColorListItemAdapter (
    private var list: ArrayList<String>,
    private val mSelectedColor : String
    ) : RecyclerView.Adapter<LabelColorListItemAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: ItemLabelColorBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(position: Int) {
            val item = list[position]

            itemBinding.viewMain.setBackgroundColor(Color.parseColor(item))

            if(item == mSelectedColor){
                itemBinding.ivSelectedColor.visibility = View.VISIBLE
            } else{
                itemBinding.ivSelectedColor.visibility = View.GONE
            }

            itemView.setOnClickListener {
                if(onClickListener != null) {
                    onClickListener!!.onClick(position, item)
                }
            }


        }
    }

    interface  OnClickListener {
        fun onClick(position: Int, color: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemLabelColorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        ) }

    override fun onBindViewHolder(holder: MainViewHolder, Position: Int) {
        holder.bindItem(Position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}