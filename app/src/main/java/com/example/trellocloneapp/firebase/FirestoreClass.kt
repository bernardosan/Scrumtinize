package com.example.trellocloneapp.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trellocloneapp.activities.MainActivity
import com.example.trellocloneapp.activities.MyProfileActivity
import com.example.trellocloneapp.activities.SignInActivity
import com.example.trellocloneapp.activities.SignUpActivity
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: com.example.trellocloneapp.models.User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    fun updateUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(com.example.trellocloneapp.models.User::class.java)!!

                when(activity){
                    is SignInActivity -> activity.signInSuccess(loggedInUser)
                    is MainActivity -> activity.updateNavigationUserDetails(loggedInUser)
                    is MyProfileActivity -> activity.setUserDataInUI(loggedInUser)
                }

            }
            .addOnFailureListener { e ->

                when(activity){
                    is SignInActivity -> activity.hideProgressDialog()
                    is MainActivity -> activity.hideProgressDialog()
                }

                Log.e("signInUser", "Failed signing in user", e)
            }
    }


    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile")
                Toast.makeText(activity, "Updated Successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                Log.e(activity.javaClass.simpleName,"Error while creating a board.")
                Toast.makeText(activity, "Error when updating profile!", Toast.LENGTH_SHORT).show()
            }


    }


    fun getCurrentUserId(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

}