package com.example.trellocloneapp.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import android.provider.Settings


object Constants {

    const val USERS: String = "users"

    const val  BOARDS: String = "boards"

    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val DESCRIPTION: String = "description"
    const val ASSIGNED_TO: String = "assignedTo"
    const val DOCUMENT_ID: String = "documentId"
    //const val CREATED_BY: String = "createdBy"
    const val TASK_LIST: String = "taskList"
    //const val CARD_LIST: String = "cardList"
    //const val STATE: String = "state"
    const val GROUPS: String = "groups"
    const val GROUP: String = "group"
    const val GROUPS_ID: String = "groupsId"
    const val ASSIGNED_BOARDS: String = "assignedBoards"
    const val GROUP_MEMBERS_ID: String = "groupMembersId"
    const val GROUP_MEMBERS_IMAGE: String = "groupMembersImage"

    const val BOARD_DETAIL: String = "board_detail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val BOARD_MEMBERS_LIST: String = "board_members_list"
    const val SELECT: String = "Select"
    const val UNSELECT: String = "Unselect"
    
    const val TASK_LIST_ITEM_POSITION: String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String = "card_list_item_position"

    const val SCRUMTINIZE_PREFERENCES = "ScrumtinizePreferences"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"
    const val FCM_BASE_URL: String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_KEY: String = "key"
    const val FCM_SERVER_KEY: String = "AAAARXAEmng:APA91bFhCew8E_HR7YY5AekTNi2m9IWFKGb0SGXCRexppF5JFnwCAul1p0CkKPydwGkZ1gu-J-4GfRDjtbafDAVfN2HrnPCSdXXN-vsi_5wUChze12ca4b6orB8kCWt8tMTUsXU9JGNc"
    const val FCM_KEY_TITLE: String = "title"
    const val FCM_KEY_MESSAGE: String = "message"
    const val FCM_KEY_DATA: String = "data"
    const val FCM_KEY_TO: String = "to"

    const val UPDATE_EMAIL_FLAG: String = "update_email_flag"

    // get files extensions from Uri
    fun getFileExtension(activity: Activity, uri: Uri?): String?{
        return MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun isReadStorageAllowed(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }


    fun showRationaleDialog(
        activity: Activity,
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(title).setMessage(message).setPositiveButton("Cancel") {
                dialog, _ -> dialog.dismiss()
        }.setNegativeButton("Settings"){
            dialog,_ ->
            dialog.dismiss()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.packageName, null)
            )
            startActivity(activity,intent, null)

        }
        builder.create().show()
    }


}