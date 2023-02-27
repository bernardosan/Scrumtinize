package com.example.trellocloneapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.GroupMembersListAdapter
import com.example.trellocloneapp.databinding.ActivityGroupMembersBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.utils.Constants



class GroupsMembersActivity : BaseActivity() {

    private var mAssignedGroupList: ArrayList<Group> = ArrayList()
    private var binding: ActivityGroupMembersBinding? = null
    private var mAdapter: GroupMembersListAdapter? = null
    private lateinit var mBoardDetails: Board

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
        } else if(result.resultCode == RESULT_CANCELED) {
            callForGroupList()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()
        callForGroupList()

        binding?.toolbarGroupMembers?.setNavigationOnClickListener {
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
                dialogSearchGroup()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarGroupMembers)
        binding?.toolbarGroupMembers?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding?.toolbarGroupMembers?.title = resources.getString(R.string.groups)
    }

    fun callForGroupList(){
        Toast.makeText(this, "group size: " + mBoardDetails.groupsId.size.toString(), Toast.LENGTH_SHORT).show()
        if(mBoardDetails.groupsId.size > 0) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getGroupsAssignedToBoard(this, mBoardDetails)
        }
    }

    fun setupGroupRecyclerView(){

        Toast.makeText(this, "groups" + mAssignedGroupList.size.toString(), Toast.LENGTH_SHORT).show()
        mAdapter = GroupMembersListAdapter(this, mAssignedGroupList)
        binding?.rvGroupsList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.rvGroupsList?.adapter = mAdapter
        binding?.rvGroupsList?.setHasFixedSize(true)


        mAdapter!!.setOnClickListener(
            object:GroupMembersListAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    startMembersActivity(mAssignedGroupList[position])
                }
            }
        )

        //val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(mAdapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
        // itemTouchHelper.attachToRecyclerView(binding?.rvGroupsList)
    }

    @SuppressLint("CutPasteId")
    private fun dialogSearchGroup(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_group)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val id = dialog.findViewById<EditText>(R.id.et_email_search_group).text.toString()
            val isGroupAlreadyAssigned = checkIfGroupAlreadyAssigned(mAssignedGroupList,id)
            if(id.isNotEmpty() && !isGroupAlreadyAssigned){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getGroup(this, id)
            } else if (isGroupAlreadyAssigned) {
                dialog.dismiss()
                showErrorSnackBar("Group already assigned!")
            } else{
                dialog.dismiss()
                showErrorSnackBar("Please enter an Group ID")
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    fun addGroup(group: Group){
        mBoardDetails.groupsId.add(group.documentId)
        group.assignedBoards.add(mBoardDetails.documentId)
        FirestoreClass().updateBoard(this, mBoardDetails, group)
    }

    fun addGroupSuccessfully(group: Group){
        hideProgressDialog()
        mAssignedGroupList.add(group)
        mAdapter?.notifyItemInserted(mAssignedGroupList.size-1)
    }

    private fun startMembersActivity(group: Group){
        val intent = Intent(this, MembersActivity::class.java)
        intent.putExtra(Constants.GROUPS, group)
        resultLauncher.launch(intent)
    }

    private fun startCreateGroupActivity(){
        resultLauncher.launch(Intent(this, CreateGroupActivity::class.java))
    }

    fun getAssignedGroupList(groupList: ArrayList<Group>){
        mAssignedGroupList = groupList
        hideProgressDialog()
        setupGroupRecyclerView()
    }

    private fun checkIfGroupAlreadyAssigned(assignedGroups: ArrayList<Group>, id: String): Boolean{
        for(i in assignedGroups){
            if(i.documentId.lowercase() == id.lowercase()){
                return true
            }
        }
        return false
    }

}