package com.example.trellocloneapp.activities

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.User
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import java.util.Arrays.asList

private lateinit var callbackManager: CallbackManager
private lateinit var facebookButton: LoginButton
private lateinit var auth: FirebaseAuth

class FacebookAuthActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()
        auth = FirebaseAuth.getInstance()


        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("FacebookLogin", "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }

            override fun onCancel() {
                Log.d("FacebookLogin", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                error.printStackTrace()
                Log.d(ContentValues.TAG, "facebook:onError", error)
            }
        })


    }

    fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val account = auth.currentUser
                    val user = User(account!!.uid, account.displayName!!, account.email!!,
                        account.photoUrl.toString()
                    )
                    FirestoreClass().registerUser(this, user)
                    FirestoreClass().updateUserData(this)
                } else {
                    // If sign in fails, display a message to the user.
                    task.exception?.printStackTrace()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}