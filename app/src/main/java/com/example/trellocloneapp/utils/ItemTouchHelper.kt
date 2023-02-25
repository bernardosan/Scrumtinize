package com.example.trellocloneapp.utils

import android.content.res.Resources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.trellocloneapp.adapters.GroupListAdapter
import com.example.trellocloneapp.adapters.TaskListAdapter

class ItemMoveCallback(private val adapter:Adapter<RecyclerView.ViewHolder>) : ItemTouchHelper.Callback(){

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlags = 0

        if(adapter is TaskListAdapter) {
            dragFlags = ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }else if (adapter is GroupListAdapter){
            dragFlags = ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }

        if(viewHolder.adapterPosition == adapter.itemCount -1){
            return 0
        }

        return makeMovementFlags(dragFlags, 0)
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.2f
    }


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        if(adapter is TaskListAdapter && toPosition < adapter.itemCount-1) {
            adapter.onItemMove(fromPosition, toPosition)
            adapter.createViewHolder(recyclerView, 0)
        } else if (adapter is GroupListAdapter){
            adapter.onItemMove(fromPosition, toPosition)
            adapter.createViewHolder(recyclerView, 0)
        }
        return true
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        if(adapter is TaskListAdapter){
            val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager)
            linearLayoutManager.scrollToPositionWithOffset(viewHolder.adapterPosition,50.toPx().toDp())
            if(!linearLayoutManager.isSmoothScrolling){
                adapter.itemMoved()
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    interface ItemTouchHelperAdapter {
        fun onItemMove(fromPosition: Int, toPosition: Int)
        fun onItemDismiss(position: Int)
    }

    private fun Int.toDp(): Int =
        (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}