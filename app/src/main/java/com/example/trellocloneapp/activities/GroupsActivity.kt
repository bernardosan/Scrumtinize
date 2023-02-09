package com.example.trellocloneapp.activities

import android.content.Intent
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
import com.example.trellocloneapp.utils.Constants
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

    private fun setupGroupList(list: ArrayList<Group>) {
        mAssignedGroupList = list

        val addGroup = Group("Add Group")
        val arrayString = ArrayList<String>()
        arrayString.add("mKyGoTiT0ledklrDAzBE2irTQwo1")
        mAssignedGroupList.add(Group("Android Devs","", arrayString))
        mAssignedGroupList.add(Group("IOS Devs", "", arrayString))
        mAssignedGroupList.add(Group("Managers","", arrayString))
        mAssignedGroupList.add(addGroup)


        val adapter = GroupListAdapter(mAssignedGroupList)

        binding?.rvGroupsList?.layoutManager = when {
            adapter.itemCount > 5 -> {
                GridLayoutManager(
                    this,
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4,
                    GridLayoutManager.VERTICAL,
                    false
                )
            }
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> {
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
            else -> {
                GridLayoutManager(
                    this,
                    2,
                    GridLayoutManager.VERTICAL,
                    false
                )
            }
        }

        binding?.rvGroupsList?.setHasFixedSize(true)
        binding?.rvGroupsList?.adapter = adapter


        adapter.setOnClickListener(
            object:GroupListAdapter.OnClickListener{
                override fun onClick(group: Group) {
                    startMembersActivity(group)
                }

            }
        )

        val itemTouchHelper =
            ItemTouchHelper(ItemMoveCallback(adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
        itemTouchHelper.attachToRecyclerView(binding?.rvGroupsList)

    }

    fun startMembersActivity(group: Group){
        val intent = Intent(this, MembersActivity::class.java)
        intent.putExtra(Constants.GROUPS, group)
        startActivity(intent)
    }




}