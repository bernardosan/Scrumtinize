package com.example.trellocloneapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity() {

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