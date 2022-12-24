package com.example.trellocloneapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityForgotPasswordBinding
import com.example.trellocloneapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private var binding: ActivityForgotPasswordBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarForgotPassword)

        if(intent.hasExtra(Constants.EMAIL)){
            binding?.etEmail?.setText(intent.getStringExtra(Constants.EMAIL))
        }

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding?.toolbarForgotPassword?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSend?.setOnClickListener {
            showProgressDialog(getString(R.string.please_wait))
            setNewPassword()
        }

    }

    private fun validateForm(email: String): Boolean {
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email address.")
                false
            } else -> {
                true
            }
        }
    }

    private fun setNewPassword(){
        val email: String = binding?.etEmail?.text.toString().trim{ it <= ' '}

        if(validateForm(email)){
            showProgressDialog(getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task -> hideProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(this, "An email has been sent to $email", Toast.LENGTH_SHORT).show()
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