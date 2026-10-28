
package com.example.nai_taro

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityRatingUlasanBinding
import org.json.JSONArray
import org.json.JSONObject

class RatingUlasanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingUlasanBinding

    private val preferencesName = "SIGAP_PREF"
    private val submissionsKey = "pengajuan_barang_list"
    private val reviewsKey = "rating_ulasan_list"

    private var submissionId = ""
    private var namaFasilitas = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRatingUlasanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submissionId = intent.getStringExtra(EXTRA_SUBMISSION_ID).orEmpty()

        if (submissionId.isBlank()) {
            Toast.makeText(
                this,
                "ID peminjaman tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        if (!validasiPeminjaman()) return

        binding.tvNamaFasilitasRating.text = namaFasilitas

        binding.ratingBarFasilitas.setOnRatingBarChangeListener {
                _, rating, _ ->
            binding.tvLabelRating.text = when (rating.toInt()) {
                1 -> "Sangat kurang"
                2 -> "Kurang"
                3 -> "Cukup"
                4 -> "Baik"
                5 -> "Sangat baik"
                else -> "Pilih bintang 1–5"
            }
        }

        binding.btnKirimUlasan.setOnClickListener {
            simpanUlasan()
        }

        binding.btnBatalUlasan.setOnClickListener {
            finish()
        }
    }

    private fun validasiPeminjaman(): Boolean {
        val prefs = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val submissions = JSONArray(
            prefs.getString(submissionsKey, "[]") ?: "[]"
        )

        var ditemukan = false

        for (i in 0 until submissions.length()) {
            val item = submissions.optJSONObject(i) ?: continue

            if (item.optString("id") == submissionId) {
                ditemukan = true

                if (item.optString("status") != "Dikembalikan") {
                    Toast.makeText(
                        this,
                        "Rating hanya tersedia setelah fasilitas dikembalikan",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    return false
                }

                namaFasilitas = item.optString("namaAset")
                    .ifBlank { item.optString("namaBarang", "Fasilitas") }

                break
            }
        }

        if (!ditemukan) {
            Toast.makeText(
                this,
                "Data peminjaman tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return false
        }

        val reviews = JSONArray(
            prefs.getString(reviewsKey, "[]") ?: "[]"
        )

        for (i in 0 until reviews.length()) {
            val review = reviews.optJSONObject(i) ?: continue

            if (review.optString("submissionId") == submissionId) {
                Toast.makeText(
                    this,
                    "Peminjaman ini sudah diberi ulasan",
                    Toast.LENGTH_LONG
                ).show()
                finish()
                return false
            }
        }

        return true
    }

    private fun simpanUlasan() {
        val rating = binding.ratingBarFasilitas.rating.toInt()
        val ulasan = binding.etUlasan.text
            ?.toString()
            ?.trim()
            .orEmpty()

        if (rating !in 1..5) {
            Toast.makeText(
                this,
                "Silakan pilih rating 1–5 bintang",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val prefs = getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val reviews = JSONArray(
            prefs.getString(reviewsKey, "[]") ?: "[]"
        )

        // Periksa ulang untuk mencegah ulasan ganda.
        for (i in 0 until reviews.length()) {
            val review = reviews.optJSONObject(i) ?: continue

            if (review.optString("submissionId") == submissionId) {
                Toast.makeText(
                    this,
                    "Ulasan untuk peminjaman ini sudah ada",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
        }

        val review = JSONObject().apply {
            put("submissionId", submissionId)
            put("namaFasilitas", namaFasilitas)
            put("rating", rating)
            put("ulasan", ulasan)
            put("dibuatPada", System.currentTimeMillis())
        }

        reviews.put(review)

        prefs.edit()
            .putString(reviewsKey, reviews.toString())
            .apply()

        Toast.makeText(
            this,
            "Rating dan ulasan berhasil disimpan",
            Toast.LENGTH_LONG
        ).show()

        setResult(RESULT_OK)
        finish()
    }

    companion object {
        const val EXTRA_SUBMISSION_ID = "extra_rating_submission_id"
    }
}
