package com.example.trellocloneapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trellocloneapp.R
import com.example.trellocloneapp.databinding.ActivityGroupsBinding

class GroupsActivity : AppCompatActivity() {
    private var binding: ActivityGroupsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        binding?.toolbarGroups?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_groups)
        setSupportActionBar(findViewById(R.id.toolbar_groups))
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title = resources.getString(R.string.groups)
    }



}