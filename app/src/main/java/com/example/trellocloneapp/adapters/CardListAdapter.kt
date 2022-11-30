package com.example.trellocloneapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.R
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemCardBinding
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Card

open class CardListAdapter(private val context: Context, private var cardList: ArrayList<Card>, private var taskPosition: Int) :
    RecyclerView.Adapter<CardListAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: ItemCardBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(cardPosition: Int) {
            val model = cardList[cardPosition]

            itemBinding.tvCardName.text = model.title


            /*
            itemBinding.ibEditCardName.setOnClickListener {

                itemBinding.etEditCardName.setText(model.title)
                itemBinding.llCardTitleView.visibility = View.GONE
                itemBinding.cvEditCardListName.visibility = View.VISIBLE

            }

            itemBinding.ibCloseEditableView.setOnClickListener {
                itemBinding.cvEditCardListName.visibility = View.GONE
                itemBinding.llCardTitleView.visibility = View.VISIBLE
            }

            itemBinding.ibDoneEditCardName.setOnClickListener {

                val cardName = itemBinding.etEditCardName.text.toString()
                if (cardName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateCard(cardPosition, taskPosition, cardName, model)
                    }
                } else {
                    Toast.makeText(context, "Please enter a card name.", Toast.LENGTH_SHORT).show()
                }
                itemBinding.llCardTitleView.visibility = View.GONE
                itemBinding.cvEditCardListName.visibility = View.VISIBLE
            }
            */


        }
    }

    interface  OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        ) }

    override fun onBindViewHolder(holder: MainViewHolder, cardPosition: Int) {
        holder.bindItem(cardPosition)

        holder.itemView.setOnClickListener {
            alertDialogForDeleteList(cardPosition, taskPosition)

        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun alertDialogForDeleteList(cardPosition: Int, taskPosition: Int){
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
    }

}