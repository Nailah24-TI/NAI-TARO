package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        // Mengambil data status login
        val sharedPreferences = getSharedPreferences(
            "SIGAP_PREF",
            MODE_PRIVATE
        )

        // Membaca status login, default false
        val isLogin = sharedPreferences.getBoolean(
            "isLogin",
            false
        )

        // Menunggu 1,5 detik sebelum berpindah halaman
        window.decorView.postDelayed({

            // Menentukan halaman tujuan berdasarkan status login
            val tujuan = if (isLogin) {
                MainActivity::class.java
            } else {
                AuthActivity::class.java
            }

            // Membuka halaman tujuan
            startActivity(Intent(this@SplashActivity, tujuan))

            // Menutup SplashActivity agar tidak kembali ke splash
            finish()

        }, 1500)
    }
}

