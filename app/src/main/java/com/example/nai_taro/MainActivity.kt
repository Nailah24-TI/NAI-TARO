
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
import com.example.nai_taro.Notifikasi.NotifikasiFragment
import com.example.nai_taro.Profil.ProfilFragment
import com.example.nai_taro.Riwayat.RiwayatFragment
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

        // Navigasi Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->

            val fragment: Fragment? = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_pengajuan -> MessageFragment()
                R.id.nav_riwayat -> RiwayatFragment()
                R.id.nav_notifikasi -> NotifikasiFragment()
                R.id.nav_profil -> ProfilFragment()
                else -> null
            }

            if (fragment != null) {
                replaceFragment(fragment)
                true
            } else {
                false
            }
        }

        // Tampilkan Beranda saat pertama kali dibuka
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    // Mengganti fragment di area konten
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Menu Toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Aksi Menu Toolbar
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
