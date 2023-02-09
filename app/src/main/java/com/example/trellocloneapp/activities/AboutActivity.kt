package com.example.trellocloneapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trellocloneapp.R

import com.example.trellocloneapp.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    var binding: ActivityAboutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupActionBar()

        binding?.toolbarAbout?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupActionBar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_about)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.title = resources.getString(R.string.about)
    }
}