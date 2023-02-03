package com.example.trellocloneapp.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivitySignUpBinding
import com.example.trellocloneapp.firebase.FirestoreClass
import com.example.trellocloneapp.models.User
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


        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed
        )

        val colors = intArrayOf(
            Color.RED,
            Color.RED,
            Color.RED,
            Color.RED,
        )

        val myList = ColorStateList(states, colors)

        return when{
            TextUtils.isEmpty(name)->{
                binding?.etName?.error = "Enter a name"
                binding?.tilName?.boxStrokeColor = Color.parseColor("#F72400")
                binding?.tilName?.setHelperTextColor(myList)
                binding?.tilName?.hintTextColor = myList
                showErrorSnackBar("Please enter a name.")
                false
            }
            TextUtils.isEmpty(email) ->{
                binding?.etEmail?.error = "Enter an valid email"
                binding?.tilEmail?.boxStrokeColor = Color.parseColor("#F72400")
                binding?.tilEmail?.setHelperTextColor( myList)
                binding?.tilEmail?.hintTextColor = myList
                showErrorSnackBar("Please enter an email address.")
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.etPassword?.error = "Enter an password"
                binding?.tilPassword?.setHelperTextColor(myList)
                binding?.tilPassword?.boxStrokeColor = Color.parseColor("#F72400")
                binding?.tilPassword?.hintTextColor = myList
                showErrorSnackBar(getString(R.string.please_enter_password))
                false
            } else -> {
                true
            }
        }
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun registerUser(){
        val name: String = binding?.etName?.text.toString().trim{ it <= ' '}
        val email: String = binding?.etEmail?.text.toString().trim{ it <= ' '}
        val password: String = binding?.etPassword?.text.toString()

        if(validateForm(name, email, password)){
            showProgressDialog(getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task -> hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this, user)
                    } else {
                        showErrorSnackBar("${task.exception!!.message}")
                        hideProgressDialog()
                    }
                }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }
}