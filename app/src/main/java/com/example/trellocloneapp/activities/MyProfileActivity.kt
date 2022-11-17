package com.example.trellocloneapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityMyProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MyProfileActivity : BaseActivity() {

    private var binding: ActivityMyProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        binding?.toolbarMyProfile?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnUpdate?.setOnClickListener {
            updateUser()
        }

    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_profile)
        setSupportActionBar(findViewById(R.id.toolbar_my_profile))
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title = resources.getString(R.string.my_profile)
    }

    private fun updateUser() {
        FirebaseAuth.getInstance().uid
    }


}