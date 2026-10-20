
package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = intent.getStringExtra("nama") ?: ""
        val handphone = intent.getStringExtra("handphone") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        binding.btnVerifikasi.setOnClickListener {
            val otp = binding.etOtp.text.toString().trim()

            if (otp.isEmpty() || otp != handphone) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Verifikasi Gagal")
                    .setMessage(
                        "Kode OTP salah atau kosong. " +
                                "Masukkan nomor handphone yang didaftarkan."
                    )
                    .setPositiveButton("OK", null)
                    .show()

                return@setOnClickListener
            }

            val preferences = getSharedPreferences(
                "SIGAP_PREF",
                MODE_PRIVATE
            )

            preferences.edit()
                .putString("nama", nama)
                .putString("handphone", handphone)
                .putString("username", username)
                .putString("password", password)
                .apply()

            MaterialAlertDialogBuilder(this)
                .setTitle("Registrasi Berhasil")
                .setMessage("Akun berhasil dibuat. Silakan login.")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent(
                        this,
                        AuthActivity::class.java
                    )
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP

                    startActivity(intent)
                    finish()
                }
                .show()
        }
    }
}
