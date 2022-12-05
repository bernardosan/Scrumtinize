package com.example.trellocloneapp.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.CardMemberListItemsAdapter
import com.example.trellocloneapp.databinding.ActivityCardDetailsBinding
import com.example.trellocloneapp.dialogs.LabelColorListDialog
import com.example.trellocloneapp.dialogs.MembersListDialog
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Card
import com.example.trellocloneapp.models.SelectedMembers
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>

    private var anyChangesMade = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setupActionBar()
        populatingCardDetailsUI()
        setupSelectedMembersList()



        binding?.btnUpdateCardDetails?.setOnClickListener {
            if(binding?.etNameCardDetails?.text?.isNotEmpty()!!){
                updateCardDetails()
            } else {
                Toast.makeText(this, "Please enter a Card Name", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.toolbarCardDetailsActivity?.setOnMenuItemClickListener {
            alertDialogForDeleteList(mCardPosition,mTaskListPosition)
           true
        }

        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorsListDialog()
        }

        binding?.tvSelectMembers?.setOnClickListener {
            membersListDialog()
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        binding?.toolbarCardDetailsActivity?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding?.toolbarCardDetailsActivity?.title =
            mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition]
                .title
        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].labelColor
    }

    private fun populatingCardDetailsUI(){
        binding?.etNameCardDetails?.setText(
            mBoardDetails
                .taskList[mTaskListPosition]
                .cardList[mCardPosition]
                .title)
        binding?.etNameCardDetails?.setSelection(binding?.etNameCardDetails?.text!!.length)
        if (mSelectedColor.isNotEmpty()){
            setColor()
        } else {
            binding?.tvSelectLabelColor?.text = getString(R.string.select_color)
        }

    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()

    }

    private fun updateCardDetails(){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val card = Card(binding?.etNameCardDetails?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo,
            mSelectedColor)

        mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition] = card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)

        anyChangesMade = true
    }

    private fun deleteCard(cardPosition: Int, taskPosition: Int){
        mBoardDetails.taskList[taskPosition].cardList.removeAt(cardPosition)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun alertDialogForDeleteList(cardPosition: Int, taskPosition: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("You want to delete the card?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){
                dialogInterface, _ -> dialogInterface.dismiss()

            deleteCard(cardPosition, taskPosition)
            anyChangesMade = true
            onBackPressed()

        }
        builder.setNegativeButton("No"){
                dialogInterface, _ -> dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }

    private fun setColor(){
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
        val dialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        dialog.show()
    }

    private fun membersListDialog(){
        val cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo

        if(cardAssignedMembersList.size > 0){
            for (i in mMembersDetailList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else{
            for (i in mMembersDetailList.indices){
                mMembersDetailList[i].selected = false
            }
        }

        val dialog = object: MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cardList[mCardPosition].assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id == user.id){
                            mMembersDetailList[i].selected = false
                        }
                    }

                }

                setupSelectedMembersList()

            }
        }
        dialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMembersList =
            mBoardDetails.taskList[mTaskListPosition]
                .cardList[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMembers = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )
                    selectedMembersList.add(selectedMembers)
                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMemberListItemsAdapter(this, selectedMembersList)
            binding?.rvSelectedMembersList?.adapter = adapter
            adapter.setOnClickListener(
                object : CardMemberListItemsAdapter.OnClickListener {
                    override fun onClick(position: Int) {
                        membersListDialog()
                    }
                }
            )


        }
    }
}