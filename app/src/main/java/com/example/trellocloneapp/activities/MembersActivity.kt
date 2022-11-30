package com.example.trellocloneapp.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.MembersListAdapter
import com.example.trellocloneapp.databinding.ActivityMembersBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants

class MembersActivity : BaseActivity() {

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

    fun setupMembersList(list: ArrayList<User>){
        hideProgressDialog()

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

