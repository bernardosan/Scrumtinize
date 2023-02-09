package com.example.trellocloneapp.adapters

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.databinding.ItemGroupBinding
import com.example.trellocloneapp.models.Group
import java.util.*
import kotlin.collections.ArrayList
import com.example.trellocloneapp.utils.ItemMoveCallback


open class GroupListAdapter(private var list: ArrayList<Group>) :
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>(), ItemMoveCallback.ItemTouchHelperAdapter {

    private var onClickListener: OnClickListener? = null


    inner class GroupViewHolder(val itemBinding: ItemGroupBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        @SuppressLint("SetTextI18n")
        fun bindItem(position: Int) {
            val model = list[position]

            itemBinding.tvGroupName.text = model.title


            if(position == list.size -1) {
                itemBinding.llAddGroup.visibility = View.VISIBLE
                itemBinding.llGroupItem.visibility = View.GONE

            } else{
                itemBinding.llAddGroup.visibility = View.GONE
                itemBinding.llGroupItem.visibility = View.VISIBLE
            }



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = GroupViewHolder(
            ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        if(itemCount < 6) {
            view.itemBinding.root.setPadding(12.toDp().toPx(), 0, 12.toDp().toPx(), 0)
        }

        return view
    }


    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bindItem(position)

        holder.itemBinding.llGroupItem.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }

        holder.itemBinding.llAddGroup.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }


    }

    interface  OnClickListener{
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun getItemCount(): Int {
        return list.size
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (toPosition < itemCount - 1) {
            Collections.swap(list, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            notifyItemChanged(fromPosition)
            notifyItemChanged(toPosition)
        }
    }

    override fun onItemDismiss(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()


}