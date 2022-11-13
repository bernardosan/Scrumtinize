package com.example.trellocloneapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarSignUp)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding?.toolbarSignUp?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSignUp?.setOnClickListener {
            registerUser()
        }

    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when{
            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Please enter a name.")
                false
            }
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email address.")
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

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim { it <= ' '}
        val email: String = binding?.etEmail?.text.toString().trim { it <= ' '}
        val password: String = binding?.etPassword?.text.toString()

        if(validateForm(name, email, password)){
            showProgressDialog(getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task -> hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        Toast.makeText(
                            this,
                            "$name you have successfully registered the email address $registeredEmail",
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                        finish()
                    } else {
                        showErrorSnackBar("${task.exception!!.message}")
                    }
                }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }
}