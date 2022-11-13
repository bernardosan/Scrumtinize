package com.example.trellocloneapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarSignIn)

        auth = FirebaseAuth.getInstance()

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding?.toolbarSignIn?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSignIn?.setOnClickListener {
            signInWithEmailAndPassword()
        }


    }

    private fun signInWithEmailAndPassword(){

        val email: String = binding?.etEmail?.text.toString()
        val password: String = binding?.etPassword?.text.toString()

        if (validateForm(email, password)) {
            auth.signInWithEmailAndPassword(email, password).
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(
                        this,
                        "${user?.email} logged in!",
                        Toast.LENGTH_SHORT
                    ).show()
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
        auth.signOut()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}