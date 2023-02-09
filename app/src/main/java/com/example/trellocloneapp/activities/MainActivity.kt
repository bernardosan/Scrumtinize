package com.example.trellocloneapp.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.BoardAdapter
import com.example.trellocloneapp.databinding.ActivityMainBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.installations.FirebaseInstallations
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView


open class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    private var mUserName: String? = null
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mAdapter: BoardAdapter


    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Boards list updated!", Toast.LENGTH_SHORT).show()
            binding?.rvBoardsList?.adapter?.notifyDataSetChanged()
            FirestoreClass().updateUserData(this,readBoardsList = true)
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


        if(!isValidEmail(FirebaseAuth.getInstance().currentUser!!.email.toString())){
            intent = Intent(this, MyProfileActivity::class.java)
            intent.putExtra(Constants.UPDATE_EMAIL_FLAG, true)
            startActivity(intent)
        }

        if (!tokenUpdated) {
            FirebaseInstallations
                .getInstance()
                .getToken(true)
                .addOnSuccessListener(this@MainActivity) {
                    updateFCMToken(it.token)
                }
        }

        binding?.navView?.setNavigationItemSelectedListener(this)

        binding?.dev?.movementMethod = LinkMovementMethod.getInstance()

        binding?.fabAddBoard?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            resultLauncher.launch(intent)
        }

    }

    override fun onResume() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserData(this, true)
        super.onResume()

    }


    fun boardsListToUI(boardsList: ArrayList<Board>) {

        if (boardsList.size > 0) {
            mAdapter = BoardAdapter(boardsList, this)

            binding?.rvBoardsList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding?.rvBoardsList?.adapter = mAdapter

            binding?.rvBoardsList?.visibility = View.VISIBLE
            binding?.tvNoBoardsAvailable?.visibility = View.GONE

            binding?.rvBoardsList?.setHasFixedSize(true)


            mAdapter.enableItemSwipeToDelete(this, binding?.rvBoardsList!!, boardsList)

            mAdapter.enableItemSwipeToEdit(this, binding?.rvBoardsList!!)

            mAdapter.setOnClickListener(object : BoardAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

            /*mAdapter.setOnLongClickListener(object : BoardAdapter.OnLongClickListener {
                override fun onLongClick(position: Int, model: Board) {
                    alertDialogForRemoveMember(model, position)

                }
            })*/

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

            R.id.nav_groups ->{
                val intent = Intent(this, GroupsActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_about ->{
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)

        return true
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        mUserName = user.name

        val tvUserName = findViewById<TextView>(R.id.tv_username)
        val tvUserDescription = findViewById<TextView>(R.id.tv_user_description)
        val navUserImage = findViewById<CircleImageView>(R.id.nav_user_image)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarNavHeader)

        hideProgressDialog()

        progressBar?.visibility = View.VISIBLE
        navUserImage?.visibility = View.INVISIBLE
        tvUserName?.text = user.name

        if (user.description.isNotEmpty() || user.description.isNotBlank()) {
            tvUserDescription?.text = user.description
        } else {
            tvUserDescription?.visibility = View.INVISIBLE
        }
        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar?.visibility = View.INVISIBLE
                    navUserImage?.visibility = View.VISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar?.visibility = View.INVISIBLE
                    navUserImage?.visibility =  View.VISIBLE
                    return false
                }
        })
            .placeholder(R.drawable.ic_board_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.


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

    fun alertDialogForRemoveMember(board: Board, position: Int) {
        mAdapter.removeItem(position)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        if(board.assignedTo.size != 1) {
            builder.setMessage("Are you sure you want to exit the board?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                if (board.assignedTo.size != 1) {
                    //mAdapter.removeItem(position)
                    board.assignedTo.remove(getCurrentUserId())
                    FirestoreClass().assignMemberToBoard(this, board)
                    FirestoreClass().getBoardsList(this)
                }
            }
        } else {
            builder.setMessage("Are you sure you want to delete ${board.name} board?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                deleteBoard(board.documentId)
            }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
            mAdapter.restoreItem(board, position)
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }


}
