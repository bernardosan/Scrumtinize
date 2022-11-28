package com.example.trellocloneapp.adapters

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
                itemBinding.llTitleView.visibility = View.GONE
                itemBinding.cvEditTaskListName.visibility = View.VISIBLE
            }

            itemBinding.tvAddCard.setOnClickListener {
                itemBinding.tvAddCard.visibility = View.GONE
                itemBinding.cvAddCard.visibility = View.VISIBLE
            }

            itemBinding.ibDoneCardName.setOnClickListener {
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

    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

}