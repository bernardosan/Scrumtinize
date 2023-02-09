package com.example.trellocloneapp.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.GroupListAdapter
import com.example.trellocloneapp.databinding.ActivityGroupsBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.utils.Constants
import com.example.trellocloneapp.utils.ItemMoveCallback

class GroupsActivity : BaseActivity() {

    private var mAssignedGroupList: ArrayList<Group> = ArrayList()
    private var binding: ActivityGroupsBinding? = null

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            mAssignedGroupList.clear()
            FirestoreClass().getGroupsAssigned(this)
            binding?.rvGroupsList?.adapter?.notifyDataSetChanged()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        callForGroupList()

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

    private fun callForGroupList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getGroupsAssigned(this)
    }

    private fun setupGroupRecyclerView(){
        val arrayList = ArrayList<String>()
        arrayList.add(getCurrentUserId())
        // val addGroup1 = Group(title = "Android", groupMembersId = arrayList)
        val addGroup = Group("Add Group")
        // mAssignedGroupList.add(addGroup1)
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

        binding?.rvGroupsList?.adapter = adapter
        binding?.rvGroupsList?.setHasFixedSize(true)


        adapter.setOnClickListener(
            object:GroupListAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    if(position != adapter.itemCount -1) {
                        startMembersActivity(mAssignedGroupList[position])
                    } else {
                       startCreateGroupActivity()
                    }
                }
            }
        )

        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
        itemTouchHelper.attachToRecyclerView(binding?.rvGroupsList)
    }

    private fun startMembersActivity(group: Group){
        val intent = Intent(this, MembersActivity::class.java)
        intent.putExtra(Constants.GROUPS, group)
        startActivity(intent)
    }

    private fun startCreateGroupActivity(){
        resultLauncher.launch(Intent(this@GroupsActivity, CreateGroupActivity::class.java))
    }

    fun getAssignedGroupList(groupList: ArrayList<Group>){
        mAssignedGroupList = groupList
        hideProgressDialog()
        setupGroupRecyclerView()
    }


}