package com.example.trellocloneapp.activities

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.MembersListAdapter
import com.example.trellocloneapp.databinding.ActivityMembersBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var mAssignedMemberList: ArrayList<User>
    private lateinit var mBoardDetails: Board
    private var binding: ActivityMembersBinding? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        binding?.toolbarMembersActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersList(this,mBoardDetails.assignedTo)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            }else{
                Toast.makeText(this, "Please enter email address.", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetails,user)
    }

    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMemberList.add(user)
        setupMembersList(mAssignedMemberList)
    }

    fun setupMembersList(list: ArrayList<User>){
        hideProgressDialog()

        mAssignedMemberList = list

        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding?.rvMembersList?.setHasFixedSize(true)
        binding?.rvMembersList?.adapter = MembersListAdapter(this, list)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarMembersActivity)
        binding?.toolbarMembersActivity?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding?.toolbarMembersActivity?.title = resources.getString(R.string.members)
    }

}

