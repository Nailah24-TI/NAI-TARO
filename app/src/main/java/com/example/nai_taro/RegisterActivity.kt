
package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityRegisterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali ke halaman Login
        binding.btnKembaliLogin.setOnClickListener {
            finish()
        }

        // Tombol untuk melanjutkan registrasi
        binding.btnDaftar.setOnClickListener {

            val nama = binding.etNama.text.toString().trim()
            val handphone =
                binding.etHandphone.text.toString().trim()
            val username =
                binding.etUsername.text.toString().trim()
            val password =
                binding.etPassword.text.toString().trim()

            // Validasi seluruh input
            if (
                nama.isEmpty() ||
                handphone.isEmpty() ||
                username.isEmpty() ||
                password.isEmpty()
            ) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Registrasi Gagal")
                    .setMessage("Semua data wajib diisi.")
                    .setPositiveButton("OK", null)
                    .show()

                return@setOnClickListener
            }

            // Kirim data ke halaman Verifikasi OTP
            val intent = Intent(
                this,
                VerificationActivity::class.java
            )

            intent.putExtra("nama", nama)
            intent.putExtra("handphone", handphone)
            intent.putExtra("username", username)
            intent.putExtra("password", password)

            startActivity(intent)
        }
    }
}
