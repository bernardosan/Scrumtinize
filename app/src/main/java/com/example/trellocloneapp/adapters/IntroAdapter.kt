package com.example.trellocloneapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.databinding.ItemIntroBinding
import com.example.trellocloneapp.models.ItemModel


class IntroAdapter(private val introList: List<ItemModel>):RecyclerView.Adapter<IntroAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemIntroBinding): RecyclerView.ViewHolder(binding.root){

        fun bindItem(model: ItemModel){
            binding.descTv.text = model.desc
            binding.titleTv.text= model.title
            binding.iconIv.setImageResource(model.image)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemIntroBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(introList[position])
    }

    override fun getItemCount(): Int {
        return introList.size
    }
}
