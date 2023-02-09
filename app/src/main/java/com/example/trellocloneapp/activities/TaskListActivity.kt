package com.example.trellocloneapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.TaskListAdapter
import com.example.trellocloneapp.databinding.ActivityTaskListBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.Card
import com.example.trellocloneapp.models.Task
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.example.trellocloneapp.utils.ItemMoveCallback

class TaskListActivity :BaseActivity() {

    private var binding: ActivityTaskListBinding? = null
    private var mBoardDocumentId: String = ""
    private lateinit var mBoardDetails: Board
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // val data: Intent? = result.data
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetails(this, mBoardDocumentId)

    }

    fun boardDetails(board: Board){
        mBoardDetails = board

        hideProgressDialog()
        setupActionBar(mBoardDetails)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersList(this, mBoardDetails.assignedTo)
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
        val task = Task(listName, model.createdBy, model.cardList, countListWeight(position))
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
                resultLauncher.launch(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun createCard(cardName: String, taskPosition: Int){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName,FirestoreClass().getCurrentUserId(), cardAssignedUsersList,"",0L,0)
        val cardsList = mBoardDetails.taskList[taskPosition].cardList

        cardsList.add(card)

        val task = Task(
            mBoardDetails.taskList[taskPosition].title,
            mBoardDetails.taskList[taskPosition].createdBy,
            cardsList,
            countListWeight(taskPosition)
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

    fun cardDetails(taskPosition: Int, cardPosition: Int){
        intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMemberDetailList)
        resultLauncher.launch(intent)
    }

    fun boardMembersDetailsList(list: ArrayList<User>){
        mAssignedMemberDetailList = list

        hideProgressDialog()

        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetails.taskList.add(addTaskList)

        val adapter = TaskListAdapter(this, mBoardDetails.taskList)
        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)
        binding?.rvTaskList?.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(ItemMoveCallback(adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>))
        itemTouchHelper.attachToRecyclerView(binding?.rvTaskList)
    }

    private fun countBoardWeight(): Int{
        var weight = 0
        for(i in mBoardDetails.taskList.indices){
            for(j in mBoardDetails.taskList[i].cardList.indices){
                weight += mBoardDetails.taskList[i].cardList[j].weight
            }
        }
        return weight
    }

    private fun countListWeight(taskPosition: Int): Int{
        var weight = 0
        for(i in mBoardDetails.taskList[taskPosition].cardList.indices){
                weight += mBoardDetails.taskList[taskPosition].cardList[i].weight
            }
        return weight
    }

}