
package com.example.nai_taro

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nai_taro.Home.HomeFragment
import com.example.nai_taro.Message.MessageFragment
import com.example.nai_taro.More.MoreFragment
import com.example.nai_taro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar SIGAP
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "SIGAP"
            subtitle = "Sarana & Prasarana PCR"
        }

        // Menampilkan HomeFragment saat pertama kali dibuka
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Tombol untuk berpindah Fragment
        binding.btnFragment1.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        binding.btnFragment2.setOnClickListener {
            replaceFragment(MessageFragment())
        }

        binding.btnFragment3.setOnClickListener {
            replaceFragment(MoreFragment())
        }
    }

    // Mengganti Fragment dalam fragment_container
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Menampilkan menu Toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Aksi setiap menu Toolbar
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

    // Konfirmasi Logout
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
                    this@MainActivity,
                    AuthActivity::class.java
                )

                startActivity(intent)
                finish()
            }
            .show()
    }
}
