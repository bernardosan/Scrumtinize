package com.example.trellocloneapp.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.GroupListAdapter
import com.example.trellocloneapp.databinding.ActivityGroupsBinding
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.utils.ItemMoveCallback

class GroupsActivity : BaseActivity() {

    private var mAssignedGroupList: ArrayList<Group> = ArrayList()
    private var binding: ActivityGroupsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        setupGroupList(mAssignedGroupList)

        binding?.toolbarGroups?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_groups)
        setSupportActionBar(findViewById(R.id.toolbar_groups))
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title = resources.getString(R.string.groups)
    }

    fun setupGroupList(list: ArrayList<Group>){
        mAssignedGroupList = list

        val addGroup = Group("Add Group")
        mAssignedGroupList.add(addGroup)
        mAssignedGroupList.add(addGroup)
        mAssignedGroupList.add(addGroup)
        mAssignedGroupList.add(addGroup)

        val adapter = GroupListAdapter(this, mAssignedGroupList)

        binding?.rvGroupsList?.layoutManager =
            GridLayoutManager(this,
                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4,
                GridLayoutManager.VERTICAL,
                false)
        binding?.rvGroupsList?.setHasFixedSize(true)
        binding?.rvGroupsList?.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
        itemTouchHelper.attachToRecyclerView(binding?.rvGroupsList)
    }



}