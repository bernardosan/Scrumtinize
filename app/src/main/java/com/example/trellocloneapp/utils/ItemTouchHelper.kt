package com.example.trellocloneapp.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.trellocloneapp.adapters.GroupListAdapter
import com.example.trellocloneapp.adapters.TaskListAdapter

class ItemMoveCallback(private val adapter:Adapter<RecyclerView.ViewHolder>) : ItemTouchHelper.Callback(){

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags = 0

        if(adapter is TaskListAdapter) {
            dragFlags = ItemTouchHelper.START or ItemTouchHelper.END
        } else if (adapter is GroupListAdapter){
            dragFlags = ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        if(viewHolder.adapterPosition == adapter.itemCount -1){
            return 0
        }
        return makeMovementFlags(dragFlags, 0)
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return super.getMoveThreshold(viewHolder)/2
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if(adapter is TaskListAdapter) {
            adapter.onItemMove(fromPosition, toPosition)
            adapter.createViewHolder(recyclerView, 0)
        } else if (adapter is GroupListAdapter){
            adapter.onItemMove(fromPosition, toPosition)
            adapter.createViewHolder(recyclerView, 0)
        }
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }
}