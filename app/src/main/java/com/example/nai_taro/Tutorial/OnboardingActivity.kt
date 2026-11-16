
package com.example.nai_taro.Tutorial

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

import com.example.nai_taro.AuthActivity
import com.example.nai_taro.MainActivity
import com.example.nai_taro.databinding.ActivityOnboardingBinding
import com.example.nai_taro.Tutorial.TutorialFragmentAdapter



class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private val preferencesName = "SIGAP_PREF"
    private val onboardingKey = "hasSeenOnboarding"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengaktifkan Full Screen
        hideSystemUI()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Daftar halaman onboarding
        val fragmentsList = listOf(
            Tutorial1Fragment(),
            Tutorial2Fragment(),
            Tutorial3Fragment()
        )

        // Adapter ViewPager2
        val adapter = TutorialFragmentAdapter(
            this,
            fragmentsList
        )

        binding.onboardingViewPager.adapter = adapter

        // Indikator halaman
        binding.onboardingViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    binding.tvIndicator.text = when (position) {
                        0 -> "● ○ ○"
                        1 -> "○ ● ○"
                        else -> "○ ○ ●"
                    }

                    binding.btnNext.text =
                        if (position == fragmentsList.size - 1) {
                            "Mulai Sekarang"
                        } else {
                            "Selanjutnya"
                        }
                }
            }
        )

        // Tombol Selanjutnya
        binding.btnNext.setOnClickListener {

            val currentPosition =
                binding.onboardingViewPager.currentItem

            if (currentPosition < fragmentsList.size - 1) {

                binding.onboardingViewPager.currentItem =
                    currentPosition + 1

            } else {
                selesaiOnboarding()
            }
        }

        // Tombol Lewati
        binding.btnSkip.setOnClickListener {
            selesaiOnboarding()
        }
    }

    // Menyembunyikan status bar dan navigation bar
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
    }

    // Menjaga tampilan tetap Full Screen
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            hideSystemUI()
        }
    }

    // Menyelesaikan onboarding
    private fun selesaiOnboarding() {

        val prefs = getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )

        prefs.edit()
            .putBoolean(onboardingKey, true)
            .apply()

        // Memeriksa status login
        val isLogin = prefs.getBoolean("isLogin", false)

        val tujuan = if (isLogin) {
            MainActivity::class.java
        } else {
            AuthActivity::class.java
        }

        startActivity(Intent(this, tujuan))
        finish()
    }
}
