package com.example.trellocloneapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityBaseBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private var binding: ActivityBaseBinding? = null

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    fun showProgressDialog(text: String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val tvProgressText = mProgressDialog.findViewById<TextView>(R.id.tv_progress_text)
        tvProgressText.text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    @SuppressLint("NewApi", "SimpleDateFormat")
    fun getCurrentDate(): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return LocalDateTime.now().format(formatter)
        } else {
            return SimpleDateFormat("dd/MM/yyyy").format(Date())
        }

    }

    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Click once again to exit.", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    fun showErrorSnackBar(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error))
        snackBar.show()
    }

}