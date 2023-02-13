package com.example.trellocloneapp.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ItemSelectedMemberBinding
import com.example.trellocloneapp.models.SelectedMembers

open class CardMemberListItemsAdapter (
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private var assignMembers: Boolean
) : RecyclerView.Adapter<CardMemberListItemsAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: ItemSelectedMemberBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(position: Int) {
            val item = list[position]
            var load_image = false

            if (position == list.size - 1 && assignMembers) {
                itemBinding.ivAddMember.visibility = View.VISIBLE
                itemBinding.ivSelectedMemberImage.visibility = View.GONE
                itemBinding.progressBar.visibility = View.GONE
            } else {
                itemBinding.ivAddMember.visibility = View.GONE
                itemBinding.ivSelectedMemberImage.visibility = View.GONE
                itemBinding.progressBar.visibility = View.VISIBLE
                Log.d("MEMBERS",item.image)
                load_image = true
            }

            if (load_image) {
                Glide
                    .with(context)
                    .load(item.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            itemBinding.progressBar.visibility = View.GONE
                            itemBinding.ivSelectedMemberImage.visibility = View.VISIBLE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            itemBinding.progressBar.visibility = View.GONE
                            itemBinding.ivSelectedMemberImage.visibility = View.VISIBLE
                            return false
                        }

                    })
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(itemBinding.ivSelectedMemberImage)

            }
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
            ItemSelectedMemberBinding.inflate(
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

    fun addItem(selectedMembers: SelectedMembers){
        list.add(selectedMembers)
    }

}