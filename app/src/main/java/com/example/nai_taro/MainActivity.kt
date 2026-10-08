
package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "SIGAP"
            subtitle = "Sarana & Prasarana PCR"
        }

        binding.btnWebsite.setOnClickListener {
            startActivity(
                Intent(this, WebActivity::class.java)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_search -> {
                AlertDialog.Builder(this)
                    .setTitle("Search")
                    .setMessage("Fitur pencarian belum tersedia.")
                    .setPositiveButton("OK", null)
                    .show()
                true
            }

            R.id.action_settings -> {
                AlertDialog.Builder(this)
                    .setTitle("Settings")
                    .setMessage("Pengaturan belum tersedia.")
                    .setPositiveButton("OK", null)
                    .show()
                true
            }

            R.id.action_developer -> {
                startActivity(
                    Intent(this, DeveloperActivity::class.java)
                )
                true
            }

            R.id.action_logout -> {
                showLogoutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setNegativeButton("Tidak", null)
            .setPositiveButton("Ya") { _, _ ->

                val sharedPreferences =
                    getSharedPreferences("SIGAP_PREF", MODE_PRIVATE)

                sharedPreferences.edit()
                    .putBoolean("isLogin", false)
                    .apply()

                val intent = Intent(
                    this,
                    AuthActivity::class.java
                )

                startActivity(intent)
                finish()
            }
            .show()
    }
}
