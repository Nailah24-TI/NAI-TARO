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

        // =========================
        // TOOLBAR
        // =========================

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "SIGAP"
            subtitle = "Sarana & Prasarana PCR"
        }

        // =========================
        // WEBSITE PCR
        // =========================

        binding.btnWebsite.setOnClickListener {

            val intent = Intent(
                this@MainActivity,
                WebActivity::class.java
            )

            startActivity(intent)
        }
    }

    // =========================
    // OPTION MENU
    // =========================

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(
            R.menu.main_menu,
            menu
        )

        return true
    }

    // =========================
    // MENU CLICK
    // =========================

    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        return when (item.itemId) {

            R.id.action_search -> {
                true
            }

            R.id.action_settings -> {
                true
            }

            R.id.action_logout -> {

                showLogoutDialog()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // =========================
    // LOGOUT DIALOG
    // =========================

    private fun showLogoutDialog() {

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setNegativeButton("Tidak", null)
            .setPositiveButton("Ya") { _, _ ->

                val sharedPreferences =
                    getSharedPreferences(
                        "SIGAP_PREF",
                        MODE_PRIVATE
                    )

                sharedPreferences.edit()
                    .putBoolean("isLogin", false)
                    .apply()

                val intent = Intent(
                    this@MainActivity,
                    AuthActivity::class.java
                )

                startActivity(intent)

                finish()
            }
            .show()
    }
}