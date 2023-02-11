package com.example.trellocloneapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
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

import androidx.activity.result.ActivityResultLauncher
import com.example.trellocloneapp.models.Board


class GroupsActivity : BaseActivity() {

    private var mAssignedGroupList: ArrayList<Group> = ArrayList()
    private var binding: ActivityGroupsBinding? = null
    private lateinit var mAdapter: GroupListAdapter

    var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode  == RESULT_OK) {
            Toast.makeText(this, "Group Updated!", Toast.LENGTH_SHORT).show()
            val group: Group = result.data!!.getParcelableExtra(Constants.GROUPS)!!
            mAssignedGroupList.find { it.documentId == group.documentId }?.groupMembersId =
                group.groupMembersId
            callForGroupList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")

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


        mAdapter = GroupListAdapter(mAssignedGroupList)

        binding?.rvGroupsList?.layoutManager = when {
            mAdapter.itemCount > 5 -> {
                GridLayoutManager(
                    this,
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4,
                    GridLayoutManager.VERTICAL,
                    false
                )
            }
            mAdapter.itemCount < 2 || resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT -> {
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

        binding?.rvGroupsList?.adapter = mAdapter
        binding?.rvGroupsList?.setHasFixedSize(true)


        mAdapter.setOnClickListener(
            object:GroupListAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    if(position != mAdapter.itemCount -1) {
                        startMembersActivity(mAssignedGroupList[position])
                    } else {
                       startCreateGroupActivity()
                    }
                }
            }
        )

        mAdapter.setOnLongClickListener(
            object:GroupListAdapter.OnLongClickListener{
                override fun onLongClick(position: Int) {
                    alertDialogForRemoveGroup(mAssignedGroupList[position], position)
                }
            }
        )

        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(mAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
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

    fun alertDialogForRemoveGroup(group: Group, position: Int) {
        binding?.rvGroupsList?.adapter?.notifyItemRemoved(position)
        mAssignedGroupList.remove(group)
        mAdapter.removeItem(group, position)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        if(group.groupMembersId.size != 1) {
            builder.setMessage("Are you sure you want to exit the group?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                group.groupMembersId.remove(getCurrentUserId())
                FirestoreClass().assignMemberToGroup(this, group)
            }
        } else {
            builder.setMessage("Are you sure you want to delete ${group.title} group?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                FirestoreClass().deleteGroup(this,group.documentId)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
            mAdapter.restoreItem(group, position)
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


}