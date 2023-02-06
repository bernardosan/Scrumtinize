package com.example.trellocloneapp.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemGroupBinding
import com.example.trellocloneapp.models.Group
import java.util.*
import kotlin.collections.ArrayList
import com.example.trellocloneapp.utils.ItemMoveCallback


open class GroupListAdapter(private val context: Context, private var list: ArrayList<Group>) :
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>(), ItemMoveCallback.ItemTouchHelperAdapter {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1
    private var onClickListener: GroupListAdapter.OnClickListener? = null

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

        return view
    }


    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (toPosition < itemCount - 1) {
            Collections.swap(list, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    override fun onItemDismiss(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title list?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    interface  OnClickListener {
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: GroupListAdapter.OnClickListener){
        this.onClickListener = onClickListener
    }


}