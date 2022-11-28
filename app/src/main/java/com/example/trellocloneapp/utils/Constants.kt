package com.example.trellocloneapp.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings.Global.getString
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trellocloneapp.R
import java.io.IOException

object Constants {

    const val USERS: String = "users"

    const val  BOARDS: String = "boards"

    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"
    const val CREATED_BY: String = "createdBy"

    // get files extensions from Uri
    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        return MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun isReadStorageAllowed(activity: Activity): Boolean{
        val result = ContextCompat.checkSelfPermission(
            activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result
    }


    fun showRationaleDialog(
        activity: Activity,
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(title).setMessage(message).setPositiveButton("Cancel") {
                dialog, _ -> dialog.dismiss()
        }
        builder.create().show()
    }


}