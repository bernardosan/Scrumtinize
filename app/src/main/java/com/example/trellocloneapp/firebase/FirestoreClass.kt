package com.example.trellocloneapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trellocloneapp.activities.*
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
            .get()
            .addOnSuccessListener {
                    document->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }
                activity.hideProgressDialog()
                activity.boardsListToUI(boardList)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating the board",it)
            }
    }

    /* fun getUserNameById(id: String): String{
        var username = "username"
        mFireStore
            .collection(Constants.USERS)
            .document(id)
            .get()
            .addOnSuccessListener {
                username = it.toObject(com.example.trellocloneapp.models.User::class.java)!!.name
            }
            .addOnFailureListener {
                it.printStackTrace()
            }

        return username
    }*/

    fun createBoard(activity: CreateBoardActivity, boardInfo: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"Error writing document")
            }
    }

    fun deleteBoard(activity: MainActivity, boardId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(boardId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(activity, "Board deleted.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                it.printStackTrace()
                Toast.makeText(activity, "Failed to delete board.", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUserData(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity -> activity.signInSuccess(loggedInUser)
                    is MainActivity-> activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    is MyProfileActivity -> activity.setUserDataInUI(loggedInUser)
                }

            }
            .addOnFailureListener { e ->

                when(activity){
                    is SignInActivity -> activity.hideProgressDialog()
                    is MainActivity -> activity.hideProgressDialog()
                    is MyProfileActivity -> activity.hideProgressDialog()
                }

                Log.e("signInUser", "Failed signing in user", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile")
                Toast.makeText(activity, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"Error while creating a board.")
                Toast.makeText(activity, "Error when updating profile!", Toast.LENGTH_SHORT).show()
            }


    }

    fun getCurrentUserId(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                if(activity is TaskListActivity) {
                    activity.addUpdateTaskListSuccess()
                } else if (activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                } else if (activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the task list")
            }

    }

   /* fun addUpdateCardList(taskListActivity: TaskListActivity, board: Board, taskPosition: Int){
        val cardListHashMap = HashMap<String, Any>()
        cardListHashMap[Constants.CARD_LIST] = board.taskList[taskPosition].cardList


        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(cardListHashMap)
            .addOnSuccessListener {
                taskListActivity.addUpdateCardListSuccess()
            }
            .addOnFailureListener {
                it.printStackTrace()
                taskListActivity.hideProgressDialog()
                Log.e(taskListActivity.javaClass.simpleName, "Error while updating the task list")
            }

    }*/

    fun getBoardDetails(taskListActivity: TaskListActivity, boardDocumentId: String) {

        mFireStore.collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(taskListActivity.javaClass.simpleName, document.toString())
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                taskListActivity.boardDetails(board)
            }
            .addOnFailureListener {
                taskListActivity.hideProgressDialog()
                Log.e(taskListActivity.javaClass.simpleName, "Error while creating the board list")
            }
    }

    fun getAssignedMembersList(activity: MembersActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                val usersList : ArrayList<User> = ArrayList()

                for(i in it.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                activity.setupMembersList(usersList)
            }
            .addOnFailureListener {
                it.printStackTrace()
                activity.hideProgressDialog()
                Toast.makeText(activity, "Error when loading users.", Toast.LENGTH_SHORT).show()
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){

        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                if(it.documents.size > 0){
                    val user = it.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                it.printStackTrace()
            }

    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                it.printStackTrace()
            }
    }



}