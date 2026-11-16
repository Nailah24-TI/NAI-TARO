
package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.Tutorial.OnboardingActivity


class SplashActivity : AppCompatActivity() {

    private val preferencesName = "SIGAP_PREF"
    private val onboardingKey = "hasSeenOnboarding"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val prefs = getSharedPreferences(
                preferencesName,
                MODE_PRIVATE
            )

            val hasSeenOnboarding = prefs.getBoolean(
                onboardingKey,
                false
            )

            val isLogin = prefs.getBoolean(
                "isLogin",
                false
            )

            when {
                !hasSeenOnboarding -> {
                    startActivity(
                        Intent(this, OnboardingActivity::class.java)
                    )
                }

                isLogin -> {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                }

                else -> {
                    startActivity(
                        Intent(this, AuthActivity::class.java)
                    )
                }
            }

            finish()

        }, 1500)
    }
}
