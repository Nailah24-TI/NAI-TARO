package com.example.nai_taro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            // Intent ke halaman berikutnya
        }
    }
}