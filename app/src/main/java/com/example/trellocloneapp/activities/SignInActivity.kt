package com.example.trellocloneapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivitySignInBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarSignIn)

        auth = FirebaseAuth.getInstance()

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
            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    fun signInSuccess() {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun signinGoogle() {

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
                loginWithGoogle(account.idToken)
            } catch (e: ApiException){
                e.printStackTrace()
            }
        }
    }

    private fun loginWithGoogle(token: String?) {
        val credentials = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credentials).addOnCompleteListener {
            task : Task<AuthResult> ->
            if (task.isSuccessful) {
                FirestoreClass().updateUserData(this)
            } else {
                showErrorSnackBar("${task.exception!!.message}")
            }
        }
    }


}