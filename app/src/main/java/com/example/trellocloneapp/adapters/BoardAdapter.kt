package com.example.trellocloneapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ItemBoardBinding
import com.example.trellocloneapp.models.Board
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.trellocloneapp.activities.CreateBoardActivity
import com.example.trellocloneapp.activities.MainActivity
import com.example.trellocloneapp.utils.Constants
import kotlin.collections.ArrayList


class BoardAdapter(private val boardList: ArrayList<Board>, val context: Context):RecyclerView.Adapter<BoardAdapter.MainViewHolder>() {

    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener ? = null

    inner class MainViewHolder (private val itemBinding: ItemBoardBinding) : RecyclerView.ViewHolder(itemBinding.root){
        @SuppressLint("SetTextI18n")
        fun bindItem(board: Board){
            itemBinding.tvBoardName.text = board.name
            //itemBinding.tvCreateBy.text =  "created by: " + FirestoreClass().getUserNameById(board.createdBy)
            Glide
                .with(context)
                .load(board.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(itemBinding.ivBoardImage)
        }

    }

    interface  OnClickListener{
        fun onClick(position: Int, model: Board)
    }

    interface  OnLongClickListener{
        fun onLongClick(position: Int, model: Board){
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val board = boardList[position]
        holder.bindItem(board)

        holder.itemView.setBackgroundColor(Color.WHITE)

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, board)
            }
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener!!.onLongClick(position, board)
            true
        }

    }

    override fun getItemCount(): Int {
        return boardList.size
    }

    fun removeItem(position: Int) {
        boardList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(board: Board, position: Int) {
        boardList.add(position, board)
        notifyItemInserted(position)
    }

    fun notifyEditItem(activity: MainActivity, position: Int){
        val intent = Intent(context, CreateBoardActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, boardList[position])
        activity.startActivity(intent)
        notifyItemChanged(position)
    }


    fun enableItemSwipeToDelete(context: MainActivity, recyclerView: RecyclerView, boardList: ArrayList<Board>) {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {

            private val background = ColorDrawable()
            private val backgroundColor = Color.parseColor("#f44336")
            private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
            //private val userId = FirebaseAuth.getInstance().currentUser!!.uid

            /*override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                /**
                 * To disable "swipe" for specific item return 0 here.
                 * For example:
                 * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
                 * if (viewHolder?.adapterPosition == 0) return 0
                 */
                return super.getMovementFlags(recyclerView, viewHolder)
            }*/


            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // true if moved, false otherwise
            }

            // Called when a ViewHolder is swiped by the user.
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) { // remove from adapter
                context.alertDialogForRemoveMember(boardList[viewHolder.adapterPosition], viewHolder.adapterPosition)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                //val deleteIcon = if (boardList[viewHolder.adapterPosition].assignedTo.size == 1) ContextCompat.getDrawable(context, R.drawable.ic_delete_card) else ContextCompat.getDrawable(context, R.drawable.ic_baseline_person_remove_24)
                val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_card)
                val intrinsicWidth = deleteIcon!!.intrinsicWidth
                val intrinsicHeight = deleteIcon.intrinsicHeight
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    c.drawRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), clearPaint)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                // Draw the red delete background
                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        })

        /*Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
    attached to a RecyclerView, it will first detach from the previous one.*/
        helper.attachToRecyclerView(recyclerView)
    }


    fun enableItemSwipeToEdit(activity: MainActivity, recyclerView: RecyclerView) {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {


            private val editIcon = ContextCompat.getDrawable(activity.applicationContext, R.drawable.ic_edit_white_24dp)
            private val intrinsicWidth = editIcon!!.intrinsicWidth
            private val intrinsicHeight = editIcon!!.intrinsicHeight
            private val background = ColorDrawable()
            private val backgroundColor = Color.parseColor("#24AE05")
            private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                /**
                 * To disable "swipe" for specific item return 0 here.
                 * For example:
                 * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
                 * if (viewHolder?.adapterPosition == 0) return 0
                 */
                //if (viewHolder.adapterPosition == 10) return 0
                return super.getMovementFlags(recyclerView, viewHolder)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                notifyEditItem(activity,viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                // Draw the green edit background
                background.color = backgroundColor
                background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
                background.draw(c)

                // Calculate position of edit icon
                val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val editIconMargin = (itemHeight - intrinsicHeight)
                val editIconLeft = itemView.left + editIconMargin - intrinsicWidth
                val editIconRight = itemView.left + editIconMargin
                val editIconBottom = editIconTop + intrinsicHeight

                // Draw the delete icon
                editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
                editIcon.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
                c?.drawRect(left, top, right, bottom, clearPaint)
            }

        })

        helper.attachToRecyclerView(recyclerView)
    }


}