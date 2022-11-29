package com.example.trellocloneapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.activities.TaskListActivity
import com.example.trellocloneapp.databinding.ItemBoardBinding
import com.example.trellocloneapp.databinding.ItemTaskBinding
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Task

open class TaskListAdapter(private val context: Context, private var list: ArrayList<Task>) :
    RecyclerView.Adapter<TaskListAdapter.MainViewHolder>(){

    inner class MainViewHolder (val itemBinding: ItemTaskBinding) : RecyclerView.ViewHolder(itemBinding.root){

        fun bindItem(position: Int) {
            val model = list[position]
            if(position == list.size -1) {
                itemBinding.tvAddTaskList.visibility = View.VISIBLE
                itemBinding.llTaskItem.visibility = View.GONE

            } else{
                itemBinding.tvAddTaskList.visibility = View.GONE
                itemBinding.llTaskItem.visibility = View.VISIBLE
            }

            itemBinding.tvTaskListTitle.text = model.title
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



            itemBinding.tvAddCard.setOnClickListener {
                itemBinding.tvAddCard.visibility = View.GONE
                itemBinding.cvAddCard.visibility = View.VISIBLE
            }

            itemBinding.ibCloseCardName.setOnClickListener {
                itemBinding.tvAddCard.visibility = View.VISIBLE
                itemBinding.cvAddCard.visibility = View.GONE
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = MainViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        val layoutParams = LinearLayout.LayoutParams((parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp()).toPx(),0,(40.toDp()).toPx(),0)

        view.itemBinding.root.layoutParams = layoutParams

        return view
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindItem(position)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    private fun alertDialogForDeleteList(position: Int, title: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title task?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){
            dialogInterface, which -> dialogInterface.dismiss()

            if(context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No"){
            dialogInterface, which -> dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

}