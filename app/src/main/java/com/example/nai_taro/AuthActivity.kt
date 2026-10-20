
package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = getSharedPreferences(
            "SIGAP_PREF",
            MODE_PRIVATE
        )

        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text
                .toString().trim()

            val password = binding.etPassword.text
                .toString().trim()

            val savedUsername = preferences.getString(
                "username", ""
            ) ?: ""

            val savedPassword = preferences.getString(
                "password", ""
            ) ?: ""

            val loginDefault =
                username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        username == password

            val loginRegistered =
                username.isNotEmpty() &&
                        password.isNotEmpty() &&
                        username == savedUsername &&
                        password == savedPassword

            if (loginDefault || loginRegistered) {
                preferences.edit()
                    .putBoolean("isLogin", true)
                    .apply()

                startActivity(
                    Intent(this, MainActivity::class.java)
                )
                finish()
            } else {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Login Gagal")
                    .setMessage(
                        "Username atau password salah. Silakan coba lagi."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
}
