package com.example.trellocloneapp.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.trellocloneapp.R
import com.example.trellocloneapp.adapters.CardMemberListItemsAdapter
import com.example.trellocloneapp.databinding.ActivityCreateGroupBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.Group
import com.example.trellocloneapp.models.SelectedMembers
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.io.IOException

class CreateGroupActivity : BaseActivity() {
    private var binding: ActivityCreateGroupBinding? = null
    private var mSelectedImageFileUri: Uri? = null
    private var mGroupImageURL: String = ""

    private var mUser: User? = null

    private val mAssignedMembers = ArrayList<User>()
    private val assignedMembersId = ArrayList<String>()
    private val assignedMembersImage = ArrayList<String>()
    private lateinit var mAdapter: CardMemberListItemsAdapter
    private val selectedMembersList = ArrayList<SelectedMembers> ()


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
                    .into(findViewById(R.id.iv_add_group))
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

        if(intent.hasExtra(Constants.USERS)){
            mUser = intent.getParcelableExtra(Constants.USERS)!!
        }

        setupSelectedMembersList()

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
            } else if (binding?.etGroupName?.text?.isNotEmpty()!! && binding?.etGroupName?.text?.isNotBlank()!!){
                showProgressDialog(getString(R.string.please_wait))
                createGroup()
            } else {
                showErrorSnackBar(getString(R.string.enter_valid_group_name_error))
            }

        }

    }

    private fun setupSelectedMembersList() {
        /*
        for (i in mAssignedMembers.indices) {
            for (j in assignedMembersId) {
                if (mAssignedMembers[i].id == j) {
                    val selectedMembers = SelectedMembers(
                        mAssignedMembers[i].id,
                        mAssignedMembers[i].image
                    )
                    selectedMembersList.add(selectedMembers)
                }
            }
        }*/

        selectedMembersList.add(SelectedMembers(mUser!!.id, mUser!!.image))
        binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(this, 6)
        mAdapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
        binding?.rvSelectedMembersList?.adapter = mAdapter
        mAdapter.setOnClickListener(
            object : CardMemberListItemsAdapter.OnClickListener {
                override fun onClick(position: Int) {
                    dialogSearchMember()
                }
            }
        )
    }

    fun memberDetails(user: User){
        hideProgressDialog()
        mAssignedMembers.add(user)
        selectedMembersList.add(0,SelectedMembers(user.id, user.image))
        assignedMembersId.add(user.id)
        assignedMembersImage.add(user.image)
        mAdapter.notifyItemInserted(0)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString().lowercase()
            if(email.isNotEmpty() && !checkIfUserAlreadyAssigned(mAssignedMembers, email)){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            } else if ( checkIfUserAlreadyAssigned(mAssignedMembers, email)) {
                showErrorSnackBar("Member already assigned!")
            } else{
                showErrorSnackBar("Please enter email address.")
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

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
                getString(R.string.app_name) + " " + getString(R.string.need_access_external_storage))
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

        assignedMembersId.add(mUser!!.id)
        assignedMembersImage.add(mUser!!.image)


        val group = Group(
            "",
            binding?.etGroupName?.text.toString(),
            mGroupImageURL,
            getCurrentUserId(),
            ArrayList(),
            assignedMembersId,
            assignedMembersImage
        )

        FirestoreClass().createGroup(this, group)
    }

    fun groupCreatedSuccessfully(group: Group){
        hideProgressDialog()
        mAssignedMembers.forEach {
            it.groups.add(group.documentId)
            FirestoreClass().updateUserGroupList(it)
        }
        setResult(RESULT_OK, intent.putExtra(Constants.GROUPS, group))
        finish()
    }

    private fun checkIfUserAlreadyAssigned(assignedto: ArrayList<User>, email: String): Boolean{
        for(i in assignedto.indices){
            if(assignedto[i].email.lowercase() == email.lowercase()){
                return true
            }
        }
        return false
    }
}