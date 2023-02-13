package com.example.trellocloneapp.adapters


import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ItemMemberBinding
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView

open class MembersListAdapter(private val context: Context, private var membersList: ArrayList<User>) :
    RecyclerView.Adapter<MembersListAdapter.MainViewHolder>(){

    private var onClickListener: OnClickListener? = null

    inner class MainViewHolder (private val itemBinding: ItemMemberBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bindItem(cardPosition: Int) {
            val model = membersList[cardPosition]

            itemBinding.tvMemberName.text = model.name
            itemBinding.tvMemberEmail.text = model.email


            itemBinding.progressBar.visibility = View.VISIBLE
            itemBinding.ivMemberImage.visibility = View.GONE

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .listener(object : RequestListener<Drawable?>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemBinding.progressBar.visibility = View.GONE
                        itemBinding.ivMemberImage.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        itemBinding.progressBar.visibility = View.GONE
                        itemBinding.ivMemberImage.visibility = View.VISIBLE
                        return false
                    }

                })
                .placeholder(R.drawable.ic_user_place_holder)
                .into(itemBinding.ivMemberImage)

            if(model.selected){
                 itemBinding.ivSelectedMember.visibility = View.VISIBLE
            } else {
                itemBinding.ivSelectedMember.visibility = View.GONE
            }
        }
    }

    interface  OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        ) }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bindItem(position)

        holder.itemView.setOnClickListener {
            if(onClickListener != null){
                if(membersList[holder.adapterPosition].selected){
                    onClickListener!!.onClick(holder.adapterPosition, membersList[holder.adapterPosition], Constants.UNSELECT)
                } else{
                    onClickListener!!.onClick(holder.adapterPosition, membersList[holder.adapterPosition], Constants.SELECT)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    fun removeItem(position: Int) {
        membersList.removeAt(position)
        notifyItemRemoved(position)
    }



}