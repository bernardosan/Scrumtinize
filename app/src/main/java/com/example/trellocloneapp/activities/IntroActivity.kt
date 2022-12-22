package com.example.trellocloneapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import com.example.trellocloneapp.adapters.IntroAdapter
import com.example.trellocloneapp.databinding.ActivityIntroBinding
import com.example.trellocloneapp.utils.PageList

class IntroActivity : BaseActivity() {

    private var binding: ActivityIntroBinding? = null
    private var viewPager2: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupViewPager(binding!!)

        binding?.btnSignUp?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding?.btnSignIn?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

    private fun setupViewPager(binding: ActivityIntroBinding){

        val adapter = IntroAdapter(PageList.itemSlides)
        viewPager2 = binding.viewPager
        viewPager2?.adapter = adapter
        binding.dotsIndicator.setViewPager2(viewPager2!!)

    }

}