package com.example.trellocloneapp.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityMyProfileBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.IOException


class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""
    private val userHashMap = HashMap<String, Any>()
    private lateinit var mUserDetails: User


    private val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == RESULT_OK && result.data!=null){
            mSelectedImageFileUri = result.data?.data
            binding?.ivMyProfile?.setImageURI(mSelectedImageFileUri)
        }
    }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries
        }

    private val localStorageLauncher : ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
            isGranted -> if(isGranted){
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
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        if(intent.getBooleanExtra(Constants.UPDATE_EMAIL_FLAG, false)){
            requestEmailUpdate()
        }


        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserData(this@MyProfileActivity)

        binding?.toolbarMyProfile?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnUpdate?.setOnClickListener {

            if (isValidEmail(binding?.etEmailMyprofile?.text.toString())) {

                if (mSelectedImageFileUri != null) {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    uploadUserImage()
                } else {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    updateUserProfileData()
                }

            FirestoreClass().updateUserData(this)

            } else {
                showErrorSnackBar("Insert a valid email")
            }

        }

        binding?.ivMyProfile?.setOnClickListener {
            updateImageView()
        }

    }

    override fun onBackPressed() {
        if (isValidEmail(binding?.etEmailMyprofile?.text.toString())){
            super.onBackPressed()
        } else {
            showErrorSnackBar("Insert a valid email")
        }
    }

    private fun requestEmailUpdate() {
        binding?.etEmailMyprofile?.setText("")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding?.etEmailMyprofile?.focusable = View.NOT_FOCUSABLE
        }
        binding?.etEmailMyprofile?.isFocusableInTouchMode = true
        Toast.makeText(this, "Please add an valid email address.", Toast.LENGTH_LONG).show()

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

    private fun updateUserProfileData() {
        var anyChangesMade = false

        if(mSelectedImageFileUri != null){
            userHashMap[Constants.IMAGE] = mProfileImageUrl
            anyChangesMade = true
        }

        if(binding?.etNameMyprofile?.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding?.etNameMyprofile?.text.toString()
            anyChangesMade = true
        }

        if(intent.getBooleanExtra(Constants.UPDATE_EMAIL_FLAG, false)) {
            val email = binding?.etEmailMyprofile?.text.toString()
            if (isValidEmail(email)){
                userHashMap[Constants.EMAIL] = binding?.etEmailMyprofile?.text.toString()
                FirebaseAuth.getInstance().currentUser!!.updateEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Email successfully updated!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        if(it.message != null) {
                            showErrorSnackBar(it.message!!)
                        }
                    }
                anyChangesMade = true
            } else {
                showErrorSnackBar("Insert a valid email")
            }
        }
        if(binding?.etMobileMyprofile?.text.toString() != mUserDetails.mobile.toString() && !binding?.etMobileMyprofile?.text.isNullOrEmpty()){
            userHashMap[Constants.MOBILE] = binding?.etMobileMyprofile?.text.toString().toLong()
            anyChangesMade = true
        }

        if (anyChangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        } else {
            hideProgressDialog()
        }
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        finish()
    }


    private fun uploadUserImage(){

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this@MyProfileActivity, mSelectedImageFileUri)
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageUrl = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@MyProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_profile)
        setSupportActionBar(findViewById(R.id.toolbar_my_profile))
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title = resources.getString(R.string.my_profile)
    }

    fun setUserDataInUI(user: User) {

        // Initialize the user details variable
        mUserDetails = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.iv_my_profile))


        binding?.etNameMyprofile?.setText(user.name)
        binding?.etEmailMyprofile?.setText(user.email)
        if(user.mobile != 0L){
            binding?.etMobileMyprofile?.setText(user.mobile.toString())
        }

        hideProgressDialog()
    }


}