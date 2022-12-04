package com.example.trellocloneapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemCardBinding
import com.example.trellocloneapp.databinding.ItemMemberBinding
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Card
import com.example.trellocloneapp.models.User

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
        }
    }

    interface  OnClickListener {
        fun onClick(position: Int, model: Board)
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

    override fun onBindViewHolder(holder: MainViewHolder, Position: Int) {
        holder.bindItem(Position)

        holder.itemView.setOnClickListener {
            //alertDialogForDeleteList(Position)

        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    /*fun alertDialogForDeleteList(cardPosition: Int, taskPosition: Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("You want to delete the card?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){
                dialogInterface, _ -> dialogInterface.dismiss()

            if(context is TaskListActivity){
                context.deleteCard(cardPosition, taskPosition)
            }
        }
        builder.setNegativeButton("No"){
                dialogInterface, _ -> dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }*/

}