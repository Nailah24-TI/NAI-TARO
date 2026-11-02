
package com.example.nai_taro

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityDetailPeminjamanBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class DetailPeminjamanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPeminjamanBinding

    private val preferencesName = "SIGAP_PREF"
    private val submissionsKey = "pengajuan_barang_list"

    private var submissionId: String = ""
    private var submission: JSONObject? = null
    private var fotoBuktiPath: String = ""

    // Membuka kamera dan mengambil foto
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                simpanFotoBukti(bitmap)
            } else {
                Toast.makeText(
                    this,
                    "Foto tidak diambil",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Membuka galeri untuk memilih foto
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                try {
                    val bitmap = contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input)
                    }

                    if (bitmap != null) {
                        simpanFotoBukti(bitmap)
                    } else {
                        Toast.makeText(
                            this,
                            "Foto tidak dapat dibuka",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Gagal mengambil foto dari galeri",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailPeminjamanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submissionId = intent.getStringExtra(EXTRA_SUBMISSION_ID).orEmpty()

        if (submissionId.isBlank()) {
            Toast.makeText(
                this,
                "ID pengajuan tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        loadSubmission()
    }

    // Mengambil data pengajuan dari SharedPreferences
    private fun loadSubmission() {

        val preferences = getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )

        val json = preferences.getString(submissionsKey, "[]") ?: "[]"
        val submissions = JSONArray(json)

        submission = null

        for (i in 0 until submissions.length()) {
            val item = submissions.optJSONObject(i) ?: continue

            if (item.optString("id") == submissionId) {
                submission = item
                break
            }
        }

        val item = submission

        if (item == null) {
            Toast.makeText(
                this,
                "Data pengajuan tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        binding.tvDetailId.text =
            "ID Pengajuan: ${item.optString("id")}"

        binding.tvDetailNamaAset.text =
            item.optString("namaAset")
                .ifBlank { item.optString("namaBarang", "-") }

        binding.tvDetailTanggal.text =
            "Tanggal: ${item.optString("tanggal", "-")}"

        binding.tvDetailWaktu.text =
            "Waktu: ${item.optString("jamMulai", "-")} - ${item.optString("jamSelesai", "-")}"

        // Mengambil lokasi foto yang pernah disimpan
        fotoBuktiPath = item.optString("fotoBuktiPengembalian")

        if (fotoBuktiPath.isNotBlank()) {
            tampilkanFotoTersimpan()
        } else {
            binding.ivBuktiPengembalian.setImageDrawable(null)
            binding.tvStatusBukti.text = "Foto bukti belum dipilih"
            binding.tvStatusBukti.setTextColor(
                android.graphics.Color.RED
            )
        }

        generateQrCode(submissionId)
        setupCheckInOut()
        setupRating()
        updateStatusDisplay()
    }

    // Membuat QR Code berdasarkan ID transaksi
    private fun generateQrCode(content: String) {
        try {
            val size = 600

            val bitMatrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

            val bitmap = Bitmap.createBitmap(
                size,
                size,
                Bitmap.Config.RGB_565
            )

            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) {
                            android.graphics.Color.BLACK
                        } else {
                            android.graphics.Color.WHITE
                        }
                    )
                }
            }

            binding.ivQrCode.setImageBitmap(bitmap)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "QR Code gagal dibuat",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Mengatur persetujuan, check-in, foto, dan check-out
    private fun setupCheckInOut() {

        // Simulasi persetujuan
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

        // Check-in
        binding.btnCheckIn.setOnClickListener {

            val item = submission ?: return@setOnClickListener
            val status = item.optString("status", "Menunggu")

            if (status != "Disetujui") {
                Toast.makeText(
                    this,
                    "Pengajuan harus disetujui sebelum check-in",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (item.optString("waktuCheckIn").isNotBlank()) {
                Toast.makeText(
                    this,
                    "Check-in sudah dilakukan",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            item.put("waktuCheckIn", System.currentTimeMillis())
            item.put("status", "Dipinjam")

            saveSubmission(item)
            updateStatusDisplay()

            Toast.makeText(
                this,
                "Check-in berhasil",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Ambil foto menggunakan kamera
        binding.btnAmbilFoto.setOnClickListener {

            val item = submission ?: return@setOnClickListener

            if (item.optString("status") != "Dipinjam") {
                Toast.makeText(
                    this,
                    "Foto bukti hanya dapat diunggah saat barang sedang dipinjam",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            cameraLauncher.launch(null)
        }

        // Pilih foto dari galeri
        binding.btnPilihGaleri.setOnClickListener {

            val item = submission ?: return@setOnClickListener

            if (item.optString("status") != "Dipinjam") {
                Toast.makeText(
                    this,
                    "Foto bukti hanya dapat diunggah saat barang sedang dipinjam",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            galleryLauncher.launch("image/*")
        }

        // Check-out wajib memiliki foto bukti
        binding.btnCheckOut.setOnClickListener {

            val item = submission ?: return@setOnClickListener

            if (item.optString("status") != "Dipinjam") {
                Toast.makeText(
                    this,
                    "Transaksi belum dalam status Dipinjam",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val file = File(fotoBuktiPath)

            if (fotoBuktiPath.isBlank() || !file.exists()) {
                Toast.makeText(
                    this,
                    "Silakan upload foto bukti pengembalian terlebih dahulu",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // Simpan waktu dan status pengembalian
            item.put("fotoBuktiPengembalian", fotoBuktiPath)
            item.put("waktuCheckOut", System.currentTimeMillis())
            item.put("status", "Dikembalikan")

            saveSubmission(item)
            updateStatusDisplay()

            Toast.makeText(
                this,
                "Check-out berhasil. Barang telah dikembalikan.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Menyimpan foto ke penyimpanan internal aplikasi
    private fun simpanFotoBukti(bitmap: Bitmap) {

        try {
            val namaFile =
                "bukti_${submissionId.replace(Regex("[^A-Za-z0-9_-]"), "_")}.jpg"

            val file = File(filesDir, namaFile)

            FileOutputStream(file).use { output ->
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    85,
                    output
                )
            }

            fotoBuktiPath = file.absolutePath

            val item = submission ?: return

            item.put("fotoBuktiPengembalian", fotoBuktiPath)
            saveSubmission(item)

            binding.ivBuktiPengembalian.setImageBitmap(bitmap)

            binding.tvStatusBukti.text =
                "✓ Foto bukti berhasil diunggah"

            binding.tvStatusBukti.setTextColor(
                android.graphics.Color.rgb(21, 128, 61)
            )

            updateStatusDisplay()

            Toast.makeText(
                this,
                "Foto bukti berhasil disimpan",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Gagal menyimpan foto bukti",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Menampilkan foto yang sudah tersimpan
    private fun tampilkanFotoTersimpan() {

        try {
            val file = File(fotoBuktiPath)

            if (file.exists()) {

                val bitmap = BitmapFactory.decodeFile(file.absolutePath)

                if (bitmap != null) {
                    binding.ivBuktiPengembalian.setImageBitmap(bitmap)

                    binding.tvStatusBukti.text =
                        "✓ Foto bukti sudah tersedia"

                    binding.tvStatusBukti.setTextColor(
                        android.graphics.Color.rgb(21, 128, 61)
                    )
                }

            } else {
                fotoBuktiPath = ""

                binding.ivBuktiPengembalian.setImageDrawable(null)

                binding.tvStatusBukti.text =
                    "Foto bukti tidak ditemukan. Silakan upload ulang."

                binding.tvStatusBukti.setTextColor(
                    android.graphics.Color.RED
                )
            }

        } catch (e: Exception) {

            binding.tvStatusBukti.text =
                "Foto bukti tidak dapat ditampilkan"
        }
    }

    // Menyimpan perubahan transaksi ke SharedPreferences
    private fun saveSubmission(updatedItem: JSONObject) {

        val preferences = getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )

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

    // Mengatur tampilan berdasarkan status transaksi
    private fun updateStatusDisplay() {

        val item = submission ?: return
        val status = item.optString("status", "Menunggu")

        binding.tvDetailStatus.text = "Status: $status"

        // Tombol persetujuan
        binding.btnSimulasiSetujui.isEnabled =
            status == "Menunggu"

        // Tombol check-in
        binding.btnCheckIn.isEnabled =
            status == "Disetujui" &&
                    item.optString("waktuCheckIn").isBlank()

        // Foto hanya dapat diunggah ketika barang sedang dipinjam
        val bolehUploadFoto =
            status == "Dipinjam" &&
                    item.optString("waktuCheckOut").isBlank()

        binding.btnAmbilFoto.isEnabled = bolehUploadFoto
        binding.btnPilihGaleri.isEnabled = bolehUploadFoto

        // Tombol check-out aktif jika foto benar-benar tersedia
        val fileFoto = File(fotoBuktiPath)

        binding.btnCheckOut.isEnabled =
            status == "Dipinjam" &&
                    fotoBuktiPath.isNotBlank() &&
                    fileFoto.exists() &&
                    item.optString("waktuCheckOut").isBlank()

        // Status foto
        if (status == "Dipinjam" && !fileFoto.exists()) {
            binding.tvStatusBukti.text =
                "Upload foto bukti pengembalian untuk mengaktifkan Check-out."

            binding.tvStatusBukti.setTextColor(
                android.graphics.Color.RED
            )
        }

        // Rating hanya muncul setelah transaksi dikembalikan
        val sudahRating = sudahAdaUlasan(submissionId)

        binding.btnBeriRating.visibility =
            if (status == "Dikembalikan") {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

        binding.btnBeriRating.text =
            if (sudahRating) {
                "Ulasan Sudah Terkirim"
            } else {
                "Beri Rating & Ulasan"
            }

        binding.btnBeriRating.isEnabled =
            status == "Dikembalikan" && !sudahRating
    }

    // Memeriksa apakah transaksi sudah memiliki ulasan
    private fun sudahAdaUlasan(id: String): Boolean {

        val prefs = getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )

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

    // Membuka halaman Rating & Ulasan
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

                updateStatusDisplay()

                Toast.makeText(
                    this,
                    "Peminjaman ini sudah diberi ulasan",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(
                this,
                RatingUlasanActivity::class.java
            )

            intent.putExtra(
                RatingUlasanActivity.EXTRA_SUBMISSION_ID,
                submissionId
            )

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized && submissionId.isNotBlank()) {
            loadSubmission()
        }
    }

    companion object {
        const val EXTRA_SUBMISSION_ID = "extra_submission_id"
    }
}
