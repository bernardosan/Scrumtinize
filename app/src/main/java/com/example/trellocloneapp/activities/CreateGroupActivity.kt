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
import com.example.trellocloneapp.databinding.ActivityCreateGroupBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.IOException

class CreateGroupActivity : BaseActivity() {
    private var binding: ActivityCreateGroupBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mGroupImageURL: String = ""


    private val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            mSelectedImageFileUri = result.data?.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.circle_colored_border_add_image)
                    .into(findViewById(R.id.iv_add_board))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries
        }

    private val localStorageLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val pickIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            try {
                openGalleryLauncher.launch(pickIntent)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Denied Permission of storage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()

        binding?.toolbarAddGroup?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.ivAddGroup?.setOnClickListener {
            updateImageView()
        }

        binding?.btnCreateBoard?.setOnClickListener {

            if (mSelectedImageFileUri != null){
                showProgressDialog(getString(R.string.please_wait))
                uploadBoardImage()
            } else{
                showProgressDialog(getString(R.string.please_wait))
                createGroup()
            }

        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.toolbarAddGroup)
        binding?.toolbarAddGroup?.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        binding?.toolbarAddGroup?.title = resources.getString(R.string.add_board_title)
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
    private fun uploadBoardImage(){

        if(mSelectedImageFileUri != null){

            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "GROUP_IMAGE" + getCurrentUserId() + System.currentTimeMillis() +
                        "." + Constants.getFileExtension(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mGroupImageURL = uri.toString()

                    createGroup()
                    Toast.makeText(this, "Created group successfully", Toast.LENGTH_SHORT).show()

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

    private fun createGroup(){

        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserId())

        val group = Group(
            "",
            binding?.etGroupName?.text.toString(),
            mGroupImageURL,
            getCurrentUserId(),
            assignedUsersArrayList
        )

        FirestoreClass().createGroup(this, group)
    }

    fun groupCreatedSuccessfully(group: Group){
        hideProgressDialog()
        setResult(RESULT_OK, intent.putExtra(Constants.GROUPS, group))
        finish()
    }
}