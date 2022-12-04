package com.example.trellocloneapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
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

            if(model.labelColor.isNotEmpty()){
                itemBinding.viewLabelColor.visibility = View.VISIBLE
                itemBinding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                itemBinding.viewLabelColor.visibility = View.GONE
            }

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
        fun onClick(position: Int)
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
            if(onClickListener != null){
                onClickListener!!.onClick(cardPosition)
            }

        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }
}