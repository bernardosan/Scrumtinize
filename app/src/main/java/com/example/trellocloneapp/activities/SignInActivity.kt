package com.example.trellocloneapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivitySignInBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.User
import com.example.trellocloneapp.utils.Constants
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import java.util.*

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarSignIn)

        auth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()


        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.oauth_google_cloud_key)) // OAuth Web Api Key do Google Cloud
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding?.toolbarSignIn?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSignIn?.setOnClickListener {
            signInWithEmailAndPassword(
                binding?.etEmail?.text.toString().trim{ it <= ' '},
                binding?.etPassword?.text.toString()
            )
        }

        binding?.tvForgotPassword?.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            val email = binding?.etEmail?.text.toString().trim{ it <= ' '}
            intent.putExtra(Constants.EMAIL,email)
            startActivity(intent)
        }

        binding?.llSignup?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding?.btnGoogle?.setOnClickListener {
            signinGoogle()
        }

        binding?.btnFacebook?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                loginWithFacebook()
            }
        })


    }

    private fun loginWithFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this@SignInActivity, Arrays.asList("public_profile"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.d("FacebookLogin", "facebook:onSuccess:$result")
                    showProgressDialog(getString(R.string.please_wait))
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Log.d("FacebookLogin", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    error.printStackTrace()
                    Log.d("FacebookLogin", "facebook:onError", error)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun signInWithEmailAndPassword(email: String, password: String){

        if (validateForm(email, password)) {
            showProgressDialog(getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {

                        FirestoreClass().updateUserData(this)
                    } else {
                        showErrorSnackBar("${task.exception!!.message}")
                        hideProgressDialog()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar(getString(R.string.please_enter_email))
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar(getString(R.string.please_enter_password))
                false
            } else -> {
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        finish()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            //Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun signInSuccess() {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun signinGoogle() {

        val intent = googleSignInClient.signInIntent
        openActivity.launch(intent)
    }

    var openActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        result: ActivityResult ->

        if(result.resultCode == RESULT_OK){
            val int = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(int)
            try{
                val account = task.getResult(ApiException::class.java)
                showProgressDialog(getString(R.string.please_wait))
                loginWithGoogle(account)
            } catch (e: ApiException){
                e.printStackTrace()
            }
        }
    }

    private fun loginWithGoogle(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credentials).addOnCompleteListener {
            task : Task<AuthResult> ->
            if (task.isSuccessful) {
                val user = User(getCurrentUserId(), account.displayName!!, account.email!!, account.photoUrl.toString())
                FirestoreClass().registerUser(this, user)
                FirestoreClass().updateUserData(this)
            } else {
                showErrorSnackBar("${task.exception!!.message}")
            }
        }
    }

    fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    val account = task.result.user!!
                    if(account.email != null){
                        FirestoreClass().updateUserData(this)
                    } else {
                        val user = User(
                            account.uid, account.displayName.toString(), account.email.toString(),
                            account.photoUrl.toString(), 0L
                        )
                        FirestoreClass().registerUser(this, user)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    task.exception?.printStackTrace()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun requestEmailUpdateSignIn() {
        intent = Intent(this, MyProfileActivity::class.java)
        intent.putExtra(Constants.UPDATE_EMAIL_FLAG, true)
        startActivity(intent)
        hideProgressDialog()
        finish()
    }


}