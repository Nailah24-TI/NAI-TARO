
package com.example.nai_taro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityDeveloperBinding

class DeveloperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeveloperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "Profil Developer"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
