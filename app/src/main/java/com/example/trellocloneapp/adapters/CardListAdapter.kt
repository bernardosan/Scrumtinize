package com.example.trellocloneapp.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemCardBinding
import com.example.trellocloneapp.models.Card
import com.example.trellocloneapp.models.SelectedMembers

open class CardListAdapter(private val context: Context, private var cardList: ArrayList<Card>) :
    RecyclerView.Adapter<CardListAdapter.CardViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class CardViewHolder (private val itemBinding: ItemCardBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun getCardList(): ArrayList<Card> {
            return cardList
        }

        fun updateList(list: ArrayList<Card>){
            cardList = list
        }

        fun bindItem(cardPosition: Int) {
            val model = cardList[cardPosition]


            itemBinding.tvCardName.text = model.title
            itemBinding.tvCardWeight.text = model.weight.toString()


            if(model.state == 1){
                itemBinding.tvCardName.apply {
                    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or itemBinding.tvCardName.paintFlags
                    text = itemBinding.tvCardName.text
                }
            } else if (model.state == 0) {
                itemBinding.tvCardName.apply {
                    paintFlags = Paint.LINEAR_TEXT_FLAG or itemBinding.tvCardName.paintFlags
                    text = itemBinding.tvCardName.text
                }
            }

            if(model.labelColor.isNotEmpty()){
                itemBinding.viewLabelColor.visibility = View.VISIBLE
                itemBinding.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                itemBinding.viewLabelColor.visibility = View.GONE
            }

            if(model.weight > 0){
                itemBinding.tvCardWeight.visibility = View.VISIBLE
            } else {
                itemBinding.tvCardWeight.visibility = View.GONE
            }

            if ((context as TaskListActivity).mAssignedMemberDetailList.size > 0){
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for(i in context.mAssignedMemberDetailList.indices){
                    for(j in model.assignedTo){
                        if(context.mAssignedMemberDetailList[i].id == j){
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMemberDetailList[i].id,
                                context.mAssignedMemberDetailList[i].image)

                            selectedMembersList.add(selectedMembers)

                        }

                    }
                }

                if(selectedMembersList.size > 0){
                    if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                        itemBinding.rvCardSelectedMembersList.visibility = View.GONE
                    } else {
                        itemBinding.rvCardSelectedMembersList.visibility = View.VISIBLE
                        itemBinding.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context, 4)

                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)

                        itemBinding.rvCardSelectedMembersList.adapter = adapter

                        adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                                override fun onClick(position: Int) {
                                    if(onClickListener != null){
                                        onClickListener!!.onClick(position)
                                    }
                                }
                            }
                        )

                    }
                } else {
                    itemBinding.rvCardSelectedMembersList.visibility = View.GONE
                }

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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        ) }

    override fun onBindViewHolder(holder: CardViewHolder, cardPosition: Int) {
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

    fun getCard(position: Int): Card {
        return cardList[position]
    }


}