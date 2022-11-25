package com.example.trellocloneapp.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""
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

        FirestoreClass().updateUserData(this@MyProfileActivity)

        binding?.toolbarMyProfile?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnUpdate?.setOnClickListener {

            if(mSelectedImageFileUri != null){
                showProgressDialog(resources.getString(R.string.please_wait))
                uploadUserImage()
                updateUserProfileData()
            } else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }

            FirestoreClass().updateUserData(this)

        }

        binding?.ivMyProfile?.setOnClickListener {
            FirestoreClass().updateUserData(this)
            updateImageView()
        }

        // TODO: FIX BUG ON IMAGE UPDATE

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
        val userHashMap = HashMap<String, Any>()
        var anyChangesMade = false

        userHashMap[Constants.IMAGE] = mProfileImageUrl
        Log.i("URL sent", mProfileImageUrl)
        anyChangesMade = true


        if(binding?.etNameMyprofile?.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding?.etNameMyprofile?.text.toString()
            anyChangesMade = true
        }

        if(binding?.etMobileMyprofile?.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding?.etMobileMyprofile?.text.toString().toLong()
            anyChangesMade = true
        }

        if (anyChangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }

        hideProgressDialog()
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        finish()
    }


    private fun uploadUserImage(){
        if(mSelectedImageFileUri != null){

            val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + mUserDetails.id +
                        "." + Constants.getFileExtension(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot -> 
                Log.i("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageUrl = uri.toString()
                    binding?.ivMyProfile?.setImageURI(uri)

                }
            }.addOnFailureListener {
                exception ->
                Log.e("Firebase Image", "${exception.message}")
                exception.printStackTrace()
                Toast.makeText(this, "${exception.message}", Toast.LENGTH_SHORT).show()
            }
            updateUserProfileData()
            Toast.makeText(this, "Updated Successfully!", Toast.LENGTH_SHORT).show()
            hideProgressDialog()
            
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_profile)
        setSupportActionBar(findViewById(R.id.toolbar_my_profile))
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title = resources.getString(R.string.my_profile)
    }

    fun setUserDataInUI(user: User) {

        mUserDetails = user

        Log.i("image received",user.image)

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
    }


}