package com.example.trellocloneapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.TaskListAdapter
import com.example.trellocloneapp.databinding.ActivityTaskListBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Card
import com.example.trellocloneapp.models.Task
import com.example.trellocloneapp.utils.Constants

class TaskListActivity :BaseActivity() {

    private var binding: ActivityTaskListBinding? = null

    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, boardDocumentId)

    }

    fun boardDetails(board: Board){
        mBoardDetails = board

        hideProgressDialog()
        setupActionBar(mBoardDetails)

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)
        binding?.rvTaskList?.adapter = TaskListAdapter(this, board.taskList)
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)

    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)


        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun setupActionBar(board: Board) {
        setSupportActionBar(binding?.toolbarTaskListActivity)
        binding?.toolbarTaskListActivity?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding?.toolbarTaskListActivity?.title = board.name
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.action_members ->{
                val intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun createCard(cardName: String, taskPosition: Int){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName,FirestoreClass().getCurrentUserId(), cardAssignedUsersList)
        val cardsList = mBoardDetails.taskList[taskPosition].cardList

        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[taskPosition].title,
            mBoardDetails.taskList[taskPosition].createdBy,
            cardsList
        )

        mBoardDetails.taskList[taskPosition] = task


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateCard(taskPosition: Int, cardList: ArrayList<Card>){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        mBoardDetails.taskList[taskPosition].cardList = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)

    }


    fun deleteCard(cardPosition: Int, taskPosition: Int){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        mBoardDetails.taskList[taskPosition].cardList.removeAt(cardPosition)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

}