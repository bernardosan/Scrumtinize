package com.example.trellocloneapp.activities

import android.content.Intent
import android.os.Bundle
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // auth = FirebaseAuth.getInstance()

        binding?.btnSignOut?.setOnClickListener {
            signOutUser()
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
