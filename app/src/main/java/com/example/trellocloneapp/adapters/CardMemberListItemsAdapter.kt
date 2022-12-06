package com.example.trellocloneapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemCardSelectedMemberBinding
import com.example.trellocloneapp.models.SelectedMembers

open class CardMemberListItemsAdapter (
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private var assignMembers: Boolean
) : RecyclerView.Adapter<CardMemberListItemsAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(position: Int) {
            val item = list[position]

            if(position == list.size - 1 && assignMembers){
                itemBinding.ivAddMember.visibility = View.VISIBLE
                itemBinding.ivSelectedMemberImage.visibility = View.GONE
            } else{
                itemBinding.ivAddMember.visibility = View.GONE
                itemBinding.ivSelectedMemberImage.visibility = View.VISIBLE
            }

            Glide
                .with(context)
                .load(item.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(itemBinding.ivSelectedMemberImage)


        }
    }

    interface  OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemCardSelectedMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false)
        ) }

    override fun onBindViewHolder(holder: MainViewHolder, Position: Int) {
        holder.bindItem(Position)
        holder.itemView.setOnClickListener {

            if (onClickListener != null) {
                onClickListener!!.onClick(holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}