
package com.example.nai_taro

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityDetailPeminjamanBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONObject
import android.content.Intent


class DetailPeminjamanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPeminjamanBinding

    private val preferencesName = "SIGAP_PREF"
    private val submissionsKey = "pengajuan_barang_list"

    private var submissionId: String = ""
    private var submission: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailPeminjamanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submissionId = intent.getStringExtra(EXTRA_SUBMISSION_ID).orEmpty()

        if (submissionId.isBlank()) {
            Toast.makeText(this, "ID pengajuan tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadSubmission()
    }

    private fun loadSubmission() {
        val preferences = getSharedPreferences(preferencesName, MODE_PRIVATE)
        val json = preferences.getString(submissionsKey, "[]") ?: "[]"
        val submissions = JSONArray(json)

        for (i in 0 until submissions.length()) {
            val item = submissions.optJSONObject(i) ?: continue

            if (item.optString("id") == submissionId) {
                submission = item
                break
            }
        }

        val item = submission
        if (item == null) {
            Toast.makeText(this, "Data pengajuan tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvDetailId.text = "ID Pengajuan: ${item.optString("id")}"
        binding.tvDetailNamaAset.text =
            item.optString("namaAset").ifBlank { item.optString("namaBarang", "-") }
        binding.tvDetailTanggal.text = "Tanggal: ${item.optString("tanggal", "-")}"
        binding.tvDetailWaktu.text =
            "Waktu: ${item.optString("jamMulai", "-")} - ${item.optString("jamSelesai", "-")}"

        generateQrCode(submissionId)
        setupCheckInOut()
        setupRating()
        updateStatusDisplay()
    }

    private fun generateQrCode(content: String) {
        try {
            val size = 600
            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK
                        else android.graphics.Color.WHITE
                    )
                }
            }

            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "QR Code gagal dibuat", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCheckInOut() {

        binding.btnSimulasiSetujui.setOnClickListener {
            val item = submission ?: return@setOnClickListener

            if (item.optString("status", "Menunggu") != "Menunggu") {
                Toast.makeText(
                    this,
                    "Pengajuan tidak lagi berstatus Menunggu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            item.put("status", "Disetujui")
            saveSubmission(item)
            updateStatusDisplay()

            Toast.makeText(
                this,
                "Persetujuan demo berhasil",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnCheckIn.setOnClickListener {
            val item = submission ?: return@setOnClickListener
            val status = item.optString("status", "Menunggu")

            if (status != "Disetujui" && status != "Dipinjam") {
                Toast.makeText(
                    this,
                    "Demo: pengajuan harus berstatus Disetujui sebelum check-in",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (item.optString("waktuCheckIn").isNotBlank()) {
                Toast.makeText(this, "Check-in sudah dilakukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            item.put("waktuCheckIn", System.currentTimeMillis())
            item.put("status", "Dipinjam")
            saveSubmission(item)

            updateStatusDisplay()
            Toast.makeText(this, "Check-in berhasil disimulasikan", Toast.LENGTH_SHORT).show()
        }

        binding.btnCheckOut.setOnClickListener {
            val item = submission ?: return@setOnClickListener

            if (item.optString("waktuCheckIn").isBlank()) {
                Toast.makeText(this, "Lakukan check-in terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (item.optString("waktuCheckOut").isNotBlank()) {
                Toast.makeText(this, "Check-out sudah dilakukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            item.put("waktuCheckOut", System.currentTimeMillis())
            item.put("status", "Dikembalikan")
            saveSubmission(item)

            updateStatusDisplay()
            Toast.makeText(this, "Check-out berhasil disimulasikan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSubmission(updatedItem: JSONObject) {
        val preferences = getSharedPreferences(preferencesName, MODE_PRIVATE)
        val json = preferences.getString(submissionsKey, "[]") ?: "[]"
        val submissions = JSONArray(json)

        for (i in 0 until submissions.length()) {
            val item = submissions.optJSONObject(i) ?: continue

            if (item.optString("id") == submissionId) {
                submissions.put(i, updatedItem)
                break
            }
        }

        preferences.edit()
            .putString(submissionsKey, submissions.toString())
            .apply()

        submission = updatedItem
    }

    private fun updateStatusDisplay() {
        val item = submission ?: return
        val status = item.optString("status", "Menunggu")

        binding.tvDetailStatus.text = "Status: $status"

        binding.btnSimulasiSetujui.isEnabled =
            status == "Menunggu"

        binding.btnCheckIn.isEnabled =
            status == "Disetujui" && item.optString("waktuCheckIn").isBlank()

        binding.btnCheckOut.isEnabled =
            status == "Dipinjam" && item.optString("waktuCheckOut").isBlank()

        binding.btnBeriRating.visibility =
            if (status == "Dikembalikan" && !sudahAdaUlasan(submissionId)) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

        if (sudahAdaUlasan(submissionId)) {
            binding.btnBeriRating.text = "Ulasan Sudah Terkirim"
            binding.btnBeriRating.isEnabled = false
        } else {
            binding.btnBeriRating.text = "Beri Rating & Ulasan"
            binding.btnBeriRating.isEnabled = status == "Dikembalikan"
        }

    }

    private fun sudahAdaUlasan(id: String): Boolean {
        val prefs = getSharedPreferences(preferencesName, MODE_PRIVATE)
        val reviews = JSONArray(
            prefs.getString("rating_ulasan_list", "[]") ?: "[]"
        )

        for (i in 0 until reviews.length()) {
            val review = reviews.optJSONObject(i) ?: continue
            if (review.optString("submissionId") == id) {
                return true
            }
        }

        return false
    }

    private fun setupRating() {
        binding.btnBeriRating.setOnClickListener {
            val item = submission ?: return@setOnClickListener

            if (item.optString("status") != "Dikembalikan") {
                Toast.makeText(
                    this,
                    "Rating hanya tersedia setelah peminjaman selesai",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (sudahAdaUlasan(submissionId)) {
                Toast.makeText(
                    this,
                    "Peminjaman ini sudah diberi ulasan",
                    Toast.LENGTH_SHORT
                ).show()
                updateStatusDisplay()
                return@setOnClickListener
            }

            val intent = Intent(this, RatingUlasanActivity::class.java)
            intent.putExtra(
                RatingUlasanActivity.EXTRA_SUBMISSION_ID,
                submissionId
            )
            startActivity(intent)
        }
    }


    companion object {
        const val EXTRA_SUBMISSION_ID = "extra_submission_id"
    }
}
