package com.example.trellocloneapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.MembersListAdapter
import com.example.trellocloneapp.databinding.DialogListBinding
import com.example.trellocloneapp.models.User

abstract class MembersListDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
): Dialog(context){

    private var adapter: MembersListAdapter? = null
    private var binding: DialogListBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogListBinding.inflate(layoutInflater)

        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.dialog_list, binding?.root)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding?.tvTitle?.text = title
        binding?.rvList?.layoutManager = LinearLayoutManager(context)
        adapter = MembersListAdapter(context, list)
        binding?.rvList?.adapter = adapter

        adapter!!.setOnClickListener(object : MembersListAdapter.OnClickListener {
            override fun onClick(position: Int, user: User, action:String) {
                dismiss()
                onItemSelected(user, action)
            }

        })
    }

    protected abstract fun onItemSelected(user: User, action:String)
}