package com.example.trellocloneapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.MainAdapter
import com.example.trellocloneapp.databinding.ActivityMainBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.installations.FirebaseInstallations
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    private var mUserName: String? = null
    private var mUserImagePathString: String = ""
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mAdapter: MainAdapter


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        mSharedPreferences =
            this.getSharedPreferences(Constants.SCRUMTINIZE_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if (!tokenUpdated) {
            FirebaseInstallations
                .getInstance()
                .getToken(true)
                .addOnSuccessListener(this@MainActivity) {
                    updateFCMToken(it.token)
                }
        }

        binding?.navView?.setNavigationItemSelectedListener(this)

        binding?.fabAddBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            resultLauncher.launch(intent)

        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserData(this@MainActivity, true)



    }

    override fun onRestart() {
        super.onRestart()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserData(this, true)
    }


    fun boardsListToUI(boardsList: ArrayList<Board>) {

        if (boardsList.size > 0) {
            mAdapter = MainAdapter(boardsList, this)

            binding?.rvBoardsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvBoardsList?.adapter = mAdapter
            mAdapter.enableItemSwipe(this, binding?.rvBoardsList!!, boardsList)
            binding?.rvBoardsList?.visibility = View.VISIBLE
            binding?.tvNoBoardsAvailable?.visibility = View.GONE

            binding?.rvBoardsList?.setHasFixedSize(true)


            mAdapter.setOnClickListener(object : MainAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

            mAdapter.setOnLongClickListener(object : MainAdapter.OnLongClickListener {
                override fun onLongClick(position: Int, model: Board) {
                    alertDialogForDeleteBoard(model)

                }
            })

        } else {
            binding?.rvBoardsList?.visibility = View.GONE
            binding?.tvNoBoardsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(findViewById(R.id.toolbar_main_activity))
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        val drawerLayout = findViewById<DrawerLayout?>(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_sign_out -> {
                signOutUser()

                mSharedPreferences.edit().clear().apply()

                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
            }
        }

        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)

        return true
    }

    fun getImagePathString(imagePath: String){
        mUserImagePathString = imagePath
    }



    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        mUserName = user.name

        hideProgressDialog()

        // The instance of the user image of the navigation view.
        val navUserImage = findViewById<ImageView>(R.id.nav_user_image)

        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        findViewById<TextView>(R.id.tv_username).text = user.name

        if (readBoardsList) {
            FirestoreClass().getBoardsList(this)
        } else {
            hideProgressDialog()
        }

    }

    private fun deleteBoard(boardId: String) {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().deleteBoard(this, boardId)
        FirestoreClass().getBoardsList(this)
    }

    fun alertDialogForDeleteBoard(model: Board) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete ${model.name} board?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteBoard(model.documentId)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserData(this, true)
    }

    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
    }

    /*
    private fun enableItemSwipe(boardList: ArrayList<Board>) {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {

            private val deleteIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete_card)
            private val intrinsicWidth = deleteIcon!!.intrinsicWidth
            private val intrinsicHeight = deleteIcon!!.intrinsicHeight
            private val background = ColorDrawable()
            private val backgroundColor = Color.parseColor("#f44336")
            private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

            /*
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                /**
                 * To disable "swipe" for specific item return 0 here.
                 * For example:
                 * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
                 * if (viewHolder?.adapterPosition == 0) return 0
                 */

                return super.getMovementFlags(recyclerView, viewHolder)
            }
            */

            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // true if moved, false otherwise
            }

            // Called when a ViewHolder is swiped by the user.
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) { // remove from adapter
                alertDialogForDeleteBoard(boardList[viewHolder.adapterPosition])
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val isCanceled = dX == 0f && !isCurrentlyActive

                if (isCanceled) {
                    c.drawRect(itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), clearPaint)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                // Draw the red delete background
                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                // Calculate position of delete icon
                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                // Draw the delete icon
                deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon?.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        })

        /*Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
    attached to a RecyclerView, it will first detach from the previous one.*/
        helper.attachToRecyclerView(binding?.rvBoardsList)
    }

     */

}
