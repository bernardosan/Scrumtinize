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
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemTaskBinding
import com.example.trellocloneapp.models.Task
import java.util.*
import kotlin.collections.ArrayList
import com.example.trellocloneapp.utils.ItemMoveCallback


open class TaskListAdapter(private val context: Context, private var list: ArrayList<Task>) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>(), ItemMoveCallback.ItemTouchHelperAdapter{

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    inner class TaskViewHolder (val itemBinding: ItemTaskBinding) : RecyclerView.ViewHolder(itemBinding.root){

        val mList = list



        @SuppressLint("SetTextI18n")
        fun bindItem(position: Int) {
            val model = list[position]

            itemBinding.rvCardList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemBinding.rvCardList.setHasFixedSize(true)
            itemBinding.rvCardList.adapter = CardListAdapter(context, model.cardList)

            if(list.size > 0){
                itemBinding.rvCardList.visibility = View.VISIBLE
            } else{
                itemBinding.rvCardList.visibility = View.GONE
            }

            if(position == list.size -1) {
                itemBinding.tvAddTaskList.visibility = View.VISIBLE
                itemBinding.llTaskItem.visibility = View.GONE

            } else{
                itemBinding.tvAddTaskList.visibility = View.GONE
                itemBinding.llTaskItem.visibility = View.VISIBLE
            }

            val weight = countListWeight(model)

            if (weight > 0) {
                itemBinding.tvTaskListTitle.text = weight.toString() + ". " + model.title
            } else {
                itemBinding.tvTaskListTitle.text = model.title
            }


            itemBinding.tvAddTaskList.setOnClickListener {
                itemBinding.tvAddTaskList.visibility = View.GONE
                itemBinding.cvAddTaskListName.visibility = View.VISIBLE
            }

            itemBinding.ibCloseListName.setOnClickListener{
                itemBinding.cvAddTaskListName.visibility = View.GONE
                itemBinding.tvAddTaskList.visibility = View.VISIBLE
            }

            itemBinding.ibDoneListName.setOnClickListener{
                val listName = itemBinding.etTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity) {
                        context.createTaskList(listName)
                        itemBinding.cvAddTaskListName.visibility = View.GONE
                        itemBinding.llTitleView.visibility = View.VISIBLE

                    }
                } else {
                    Toast.makeText(context, "Please enter list name.", Toast.LENGTH_SHORT).show()
                }
            }

            itemBinding.ibEditListName.setOnClickListener {

                itemBinding.etEditTaskListName.setText(model.title)
                itemBinding.llTitleView.visibility = View.GONE
                itemBinding.cvEditTaskListName.visibility = View.VISIBLE

            }

            itemBinding.ibCloseEditableView.setOnClickListener{
                itemBinding.cvEditTaskListName.visibility = View.GONE
                itemBinding.llTitleView.visibility = View.VISIBLE
            }

            itemBinding.ibDoneEditListName.setOnClickListener{

                val listName = itemBinding.etEditTaskListName.text.toString()
                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                } else {
                    Toast.makeText(context, "Please enter a list name.", Toast.LENGTH_SHORT).show()
                }
                itemBinding.llTitleView.visibility = View.GONE
                itemBinding.cvEditTaskListName.visibility = View.VISIBLE
            }

            itemBinding.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }

            itemBinding.tvAddCard.setOnClickListener{

                itemBinding.etCardName.text.clear()
                itemBinding.tvAddCard.visibility = View.GONE
                itemBinding.cvAddCard.visibility = View.VISIBLE
            }

            itemBinding.ibDoneCardName.setOnClickListener {
                val cardName = itemBinding.etCardName.text.toString()
                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createCard(cardName, position)
                    }
                } else {
                    Toast.makeText(context, "Please enter a card name.", Toast.LENGTH_SHORT).show()
                }
                itemBinding.tvAddCard.visibility = View.VISIBLE
                itemBinding.cvAddCard.visibility = View.GONE
            }

            itemBinding.ibCloseCardName.setOnClickListener {
                itemBinding.tvAddCard.visibility = View.VISIBLE
                itemBinding.cvAddCard.visibility = View.GONE
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = TaskViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        val layoutParams = if(parent.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
            LayoutParams((parent.width*0.7).toInt(),LayoutParams.WRAP_CONTENT)
        } else{
            LayoutParams((parent.width*0.3462).toInt(),LayoutParams.WRAP_CONTENT)
        }

        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)

        view.itemBinding.root.layoutParams = layoutParams

        return view
    }

    override fun onBindViewHolder(holder: TaskViewHolder, @SuppressLint("RecyclerView") taskPosition: Int) {
        holder.bindItem(taskPosition)

        val adapter = CardListAdapter(context, list[holder.adapterPosition].cardList)
        holder.itemBinding.rvCardList.adapter = adapter
        adapter.setOnClickListener(
            object:CardListAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    if(context is TaskListActivity){
                        context.cardDetails(holder.adapterPosition, position)
                    }
                }
            }
        )



        //  Creates an ItemTouchHelper that will work with the given Callback.
        val helper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START or ItemTouchHelper.END) {
            /*Called when ItemTouchHelper wants to move the dragged item from its old position to
             the new position.*/

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = dragged.adapterPosition
                val targetPosition = target.adapterPosition

                if (mPositionDraggedFrom == -1) {
                    mPositionDraggedFrom = draggedPosition
                }
                mPositionDraggedTo = targetPosition

                /**
                 * Swaps the elements at the specified positions in the specified list.
                 */

                /**
                 * Swaps the elements at the specified positions in the specified list.
                 */
                Collections.swap(list[holder.adapterPosition].cardList, draggedPosition, targetPosition)

                // move item in `draggedPosition` to `targetPosition` in adapter.
                adapter.notifyItemMoved(draggedPosition, targetPosition)

                return false // true if moved, false otherwise
            }

            // Called when a ViewHolder is swiped by the user.
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) { // remove from adapter

            }

            /*Called by the ItemTouchHelper when the user interaction with an element is over and it
             also completed its animation.*/
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {

                    (context as TaskListActivity).updateCard(
                        holder.adapterPosition,
                        list[holder.adapterPosition].cardList
                    )
                }

                // Reset the global variables
                mPositionDraggedFrom = -1
                mPositionDraggedTo = -1
            }
        })

        /*Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
        attached to a RecyclerView, it will first detach from the previous one.*/
        helper.attachToRecyclerView(holder.itemBinding.rvCardList)
    }



    override fun getItemCount(): Int {
        return list.size
    }

    fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title list?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){
                dialogInterface, _ -> dialogInterface.dismiss()

            if(context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No"){
                dialogInterface, _ -> dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun countListWeight(task: Task): Int{
        var weight = 0
        for(i in task.cardList.indices){
            weight += task.cardList[i].weight
        }
        return weight
    }

    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if(toPosition < itemCount-1) {
            Collections.swap(list, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    override fun onItemDismiss(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

}