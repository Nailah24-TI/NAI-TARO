package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        val sharedPreferences =
            getSharedPreferences("SIGAP_PREF", MODE_PRIVATE)

        val isLogin =
            sharedPreferences.getBoolean("isLogin", false)

        window.decorView.postDelayed({

            if (isLogin) {

                startActivity(
                    Intent(
                        this@SplashActivity,
                        MainActivity::class.java
                    )
                )

            } else {

                startActivity(
                    Intent(
                        this@SplashActivity,
                        AuthActivity::class.java
                    )
                )
            }

            finish()

        }, 1500)
    }
}