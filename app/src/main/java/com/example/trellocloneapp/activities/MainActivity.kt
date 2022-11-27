package com.example.trellocloneapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import com.example.trellocloneapp.utils.boardListTest
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    private var mUserName: String? = null
    val assignedTo: ArrayList<String> = ArrayList()
    val documentId: String = ""

    companion object{
        const val CREATE_BOARD_REQUEST_CODE = 10
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        binding?.navView?.setNavigationItemSelectedListener(this)

        boardsListToUI(boardListTest.boardList) // TODO: FIX BUG ON FIREBASE BOARDS CALL

        binding?.fabAddBoard?.setOnClickListener{
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, 10)
        }

        FirestoreClass().updateUserData(this)

    }


    private fun boardsListToUI(boardsList: ArrayList<Board>){

        if(boardsList.size > 0){
            val adapter = MainAdapter(boardsList, this)

            binding?.rvBoardsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvBoardsList?.adapter = adapter
            binding?.rvBoardsList?.visibility = View.VISIBLE
            binding?.tvNoBoardsAvailable?.visibility = View.GONE

            binding?.rvBoardsList?.setHasFixedSize(true)

            adapter.setOnClickListener(object: MainAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == CREATE_BOARD_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            // FirestoreClass().getBoardsList(this)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_sign_out -> {
                signOutUser()
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                finish()
            }
        }

        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)

        return true
    }

    fun updateNavigationUserDetails(user: User) {

        mUserName = user.name

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.nav_user_image))

        findViewById<TextView>(R.id.tv_username).text = user.name


    }
}
