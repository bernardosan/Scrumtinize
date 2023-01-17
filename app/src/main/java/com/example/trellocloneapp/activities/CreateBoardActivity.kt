package com.example.trellocloneapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityCreateBoardBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Board
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var binding: ActivityCreateBoardBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mBoardImageURL: String = ""

    private val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == RESULT_OK && result.data!=null){
            mSelectedImageFileUri = result.data?.data

            try{
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.circle_colored_border_add_image)
                    .into(findViewById(R.id.iv_add_board))
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries
        }

    private val localStorageLauncher : ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if(isGranted){
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            openGalleryLauncher.launch(pickIntent)
        } catch (e: IOException){
            e.printStackTrace()
        }
        } else {
        Toast.makeText(this, "Denied Permission of storage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()
        FirestoreClass().updateUserData(this@CreateBoardActivity)

        //receiveIntentData()

        binding?.toolbarAddBoard?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.ivAddBoard?.setOnClickListener {
            updateImageView()
        }

        binding?.btnCreateBoard?.setOnClickListener {
            if(mSelectedImageFileUri != null) {
                uploadBoardImage()
                finish()
                FirestoreClass().updateUserData(this, true)
            } else {
                showProgressDialog(getString(R.string.please_wait))
                createBoard()
            }
        }

    }

    /*private fun receiveIntentData() {
        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
    }*/

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarAddBoard)
        binding?.toolbarAddBoard?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding?.toolbarAddBoard?.title = resources.getString(R.string.add_board_title)
    }

    private fun updateImageView() {
        requestStoragePermission()

        if(Constants.isReadStorageAllowed(this)){
            showProgressDialog(getString(R.string.please_wait))
            lifecycleScope.launch {
                localStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                hideProgressDialog()
            }
        }
    }

    private fun requestStoragePermission(){
        // Check if the permission was denied and show rationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Constants.showRationaleDialog(this, getString(R.string.app_name),
                getString(R.string.app_name) + "needs to Access Your External Storage")
        }
        else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun createBoard(){

        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        val board = Board(
            binding?.etBoardName?.text.toString(),
            mBoardImageURL,
            getCurrentUserId(),
            getCurrentDate(),
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this@CreateBoardActivity, board)

    }

    private fun uploadBoardImage(){
        showProgressDialog(getString(R.string.please_wait))
        if(mSelectedImageFileUri != null){

            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "BOARD_IMAGE" + getCurrentUserId() + System.currentTimeMillis() +
                        "." + Constants.getFileExtension(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mBoardImageURL = uri.toString()
                    createBoard()
                    Toast.makeText(this, "Created board successfully", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                    exception ->
                Log.e("Firebase Image", "${exception.message}")
                exception.printStackTrace()
                Toast.makeText(this, "${exception.message}", Toast.LENGTH_SHORT).show()
            }
            hideProgressDialog()

        }
    }

    fun boardCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        FirestoreClass().updateUserData(this, true)
        finish()
    }

}