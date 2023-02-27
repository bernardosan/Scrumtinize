package com.example.trellocloneapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ItemMemberGroupBinding
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.models.SelectedMembers
import com.example.trellocloneapp.utils.ItemMoveCallback
import java.util.*
import kotlin.collections.ArrayList

class GroupMembersListAdapter(private val context: Context, private var list: ArrayList<Group>) :
    RecyclerView.Adapter<GroupMembersListAdapter.GroupViewHolder>(), ItemMoveCallback.ItemTouchHelperAdapter {

    private var onClickListener: OnClickListener? = null


    inner class GroupViewHolder(val itemBinding: ItemMemberGroupBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        @SuppressLint("SetTextI18n")
        fun bindItem(position: Int) {
            val model = list[position]

            itemBinding.tvGroupName.text = model.title

            if(model.groupMembersId.size < 13 && model.groupMembersId.size == model.groupMembersImage.size){
                itemBinding.rvGroupMembers.visibility = View.VISIBLE
                itemBinding.tvGroupSize.visibility = View.GONE
                val selectedMembers = ArrayList<SelectedMembers>()
                for (i in model.groupMembersId.indices) {
                    selectedMembers.add(
                        SelectedMembers(
                            model.groupMembersId[i],
                            model.groupMembersImage[i]
                        )
                    )
                }

                val adapter = CardMemberListItemsAdapter(context, selectedMembers, false)
                itemBinding.rvGroupMembers.layoutManager = GridLayoutManager(context, 6)
                itemBinding.rvGroupMembers.adapter = adapter
            } else {
                itemBinding.rvGroupMembers.visibility = View.GONE
                itemBinding.tvGroupSize.visibility = View.VISIBLE
                itemBinding.tvGroupSize.text =
                    context.resources.getString(R.string.group_members_size) + ": " +
                            (model.groupMembersId.size).toString()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = GroupViewHolder(
            ItemMemberGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        if (itemCount < 6) {
            view.itemBinding.root.setPadding(12.toDp().toPx(), 0, 12.toDp().toPx(), 0)
        }

        return view
    }


    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bindItem(position)

        holder.itemBinding.cvGroupMember.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }

    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
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
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}

