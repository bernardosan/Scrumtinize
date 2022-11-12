package com.example.trellocloneapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trellocloneapp.databinding.ActivitySignInBinding
import com.example.trellocloneapp.databinding.ActivitySignUpBinding

class SignInActivity : AppCompatActivity() {

    private var binding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarSignIn)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        binding?.toolbarSignIn?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }
}