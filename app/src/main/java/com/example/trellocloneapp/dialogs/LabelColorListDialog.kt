package com.example.trellocloneapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.LabelColorListItemAdapter
import com.example.trellocloneapp.databinding.DialogListBinding

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
): Dialog(context){

    private var adapter: LabelColorListItemAdapter? = null
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
        adapter = LabelColorListItemAdapter(context, list, mSelectedColor)
        binding?.rvList?.adapter = adapter

        adapter!!.setOnClickListener(object : LabelColorListItemAdapter.OnClickListener {
            override fun onClick(position: Int, color: String) {
               dismiss()
                onItemSelected(color)

            }

        })
    }

    protected abstract fun onItemSelected(color: String)

}