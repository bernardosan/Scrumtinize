package com.example.trellocloneapp.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ItemMemberBinding
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants

open class MembersListAdapter(private val context: Context, private var membersList: ArrayList<User>) :
    RecyclerView.Adapter<MembersListAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: ItemMemberBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(cardPosition: Int) {
            val model = membersList[cardPosition]

            itemBinding.tvMemberName.text = model.name
            itemBinding.tvMemberEmail.text = model.email


            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(itemBinding.ivMemberImage)

            if(model.selected){
                 itemBinding.ivSelectedMember.visibility = View.VISIBLE
            } else {
                itemBinding.ivSelectedMember.visibility = View.GONE
            }
        }
    }

    interface  OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        ) }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindItem(position)

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                if(membersList[holder.adapterPosition].selected){
                    onClickListener!!.onClick(holder.adapterPosition, membersList[holder.adapterPosition], Constants.UNSELECT)
                } else{
                    onClickListener!!.onClick(holder.adapterPosition, membersList[holder.adapterPosition], Constants.SELECT)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }


}