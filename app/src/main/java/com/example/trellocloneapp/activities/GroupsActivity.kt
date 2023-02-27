package com.example.trellocloneapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.example.trellocloneapp.utils.ItemMoveCallback


class GroupsActivity : BaseActivity() {

    private var mAssignedGroupList: ArrayList<Group> = ArrayList()
    private var binding: ActivityGroupsBinding? = null
    private lateinit var mAdapter: GroupListAdapter
    private var mUser: User? = null

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode  == RESULT_OK) {
            Toast.makeText(this, "GroupList Updated!", Toast.LENGTH_SHORT).show()
            if(result.data?.hasExtra(Constants.GROUPS) == true) {
                val group: Group = result.data!!.getParcelableExtra(Constants.GROUPS)!!
                mAssignedGroupList.find { it.documentId == group.documentId }?.groupMembersId =
                    group.groupMembersId
            }
            callForGroupList()
        } else if(result.resultCode == RESULT_FIRST_USER){
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

        if(intent.hasExtra(Constants.USERS)){
            mUser = intent.getParcelableExtra(Constants.USERS)!!
        }

        binding?.toolbarGroups?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_group, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_group ->{
                startCreateGroupActivity()
            }
        }
        return super.onOptionsItemSelected(item)
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
        val addGroup = Group("Add Group")
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

        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(mAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
        itemTouchHelper.attachToRecyclerView(binding?.rvGroupsList)
    }

    private fun startMembersActivity(group: Group){
        val intent = Intent(this, MembersActivity::class.java)
        intent.putExtra(Constants.GROUPS, group)
        resultLauncher.launch(intent)
    }

    private fun startCreateGroupActivity(){
        val intent = Intent(this, CreateGroupActivity::class.java)
        intent.putExtra(Constants.USERS, mUser)
        resultLauncher.launch(intent)
    }

    fun getAssignedGroupList(groupList: ArrayList<Group>){
        mAssignedGroupList = groupList
        hideProgressDialog()
        setupGroupRecyclerView()
    }

}