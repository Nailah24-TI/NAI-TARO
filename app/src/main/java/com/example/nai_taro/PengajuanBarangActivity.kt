package com.example.nai_taro

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nai_taro.databinding.ActivityPengajuanBarangBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PengajuanBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengajuanBarangBinding

    private var currentStep = 1
    private val totalSteps = 4

    private val preferencesName = "SIGAP_PREF"
    private val submissionsKey = "pengajuan_barang_list"

    private var selectedBorangUri: Uri? = null
    private var selectedBorangName: String = ""

    private val borangPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Sebagian penyedia berkas tidak mendukung izin permanen.
                } catch (_: IllegalArgumentException) {
                    // URI tetap dapat digunakan selama izin yang diberikan masih berlaku.
                }

                selectedBorangUri = uri
                selectedBorangName = getFileName(uri)

                binding.tvNamaBorang.text = selectedBorangName
                binding.tvStatusBorang.text = "Borang berhasil dipilih"
                binding.btnPilihBorang.text = "Ganti Borang"
            } else {
                if (selectedBorangUri == null) {
                    binding.tvNamaBorang.text = "Belum ada berkas dipilih"
                    binding.tvStatusBorang.text = "Borang wajib dilampirkan"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPengajuanBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = "Pengajuan Barang"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnPilihBorang.setOnClickListener {
            borangPicker.launch(
                arrayOf(
                    "application/pdf",
                    "image/jpeg",
                    "image/png"
                )
            )
        }

        binding.btnPrevious.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                showStep()
            } else {
                showCancelDialog()
            }
        }

        binding.btnNext.setOnClickListener {
            when (currentStep) {
                1 -> {
                    if (validateStepOne()) {
                        currentStep = 2
                        showStep()
                    }
                }

                2 -> {
                    if (validateStepTwo()) {
                        currentStep = 3
                        showStep()
                    }
                }

                3 -> {
                    if (validateStepThree()) {
                        updateReview()
                        currentStep = 4
                        showStep()
                    }
                }

                4 -> saveSubmission()
            }
        }

        binding.etTanggal.setOnClickListener {
            showDatePicker()
        }

        binding.etWaktuMulai.setOnClickListener {
            showTimePicker(binding.etWaktuMulai)
        }

        binding.etWaktuSelesai.setOnClickListener {
            showTimePicker(binding.etWaktuSelesai)
        }

        showStep()
    }

    private fun showStep() {
        binding.stepOne.visibility =
            if (currentStep == 1) View.VISIBLE else View.GONE

        binding.stepTwo.visibility =
            if (currentStep == 2) View.VISIBLE else View.GONE

        binding.stepThree.visibility =
            if (currentStep == 3) View.VISIBLE else View.GONE

        binding.stepFour.visibility =
            if (currentStep == 4) View.VISIBLE else View.GONE

        binding.progressStep.max = totalSteps
        binding.progressStep.progress = currentStep

        binding.tvStepDescription.text = when (currentStep) {
            1 -> "Langkah 1 dari 4 • Detail Barang"
            2 -> "Langkah 2 dari 4 • Jadwal Peminjaman"
            3 -> "Langkah 3 dari 4 • Upload Borang"
            else -> "Langkah 4 dari 4 • Review Pengajuan"
        }

        binding.btnPrevious.text =
            if (currentStep == 1) "Batal" else "Kembali"

        binding.btnNext.text =
            if (currentStep == totalSteps) "Kirim Pengajuan" else "Lanjut"
    }

    private fun validateStepOne(): Boolean {
        val nama = binding.etNamaBarang.text.toString().trim()
        val jumlahText = binding.etJumlah.text.toString().trim()

        if (nama.isEmpty()) {
            binding.etNamaBarang.error = "Nama barang wajib diisi"
            binding.etNamaBarang.requestFocus()
            return false
        }

        val jumlah = jumlahText.toIntOrNull()

        if (jumlah == null || jumlah <= 0) {
            binding.etJumlah.error = "Jumlah harus lebih dari 0"
            binding.etJumlah.requestFocus()
            return false
        }

        binding.etNamaBarang.error = null
        binding.etJumlah.error = null
        return true
    }

    private fun validateStepTwo(): Boolean {
        val tanggal = binding.etTanggal.text.toString().trim()
        val mulai = binding.etWaktuMulai.text.toString().trim()
        val selesai = binding.etWaktuSelesai.text.toString().trim()
        val keperluan = binding.etKeperluan.text.toString().trim()

        if (tanggal.isEmpty()) {
            binding.etTanggal.error = "Pilih tanggal peminjaman"
            binding.etTanggal.requestFocus()
            return false
        }

        if (mulai.isEmpty()) {
            binding.etWaktuMulai.error = "Pilih waktu mulai"
            binding.etWaktuMulai.requestFocus()
            return false
        }

        if (selesai.isEmpty()) {
            binding.etWaktuSelesai.error = "Pilih waktu selesai"
            binding.etWaktuSelesai.requestFocus()
            return false
        }

        if (selesai <= mulai) {
            binding.etWaktuSelesai.error =
                "Waktu selesai harus setelah waktu mulai"
            binding.etWaktuSelesai.requestFocus()
            return false
        }

        if (keperluan.isEmpty()) {
            binding.etKeperluan.error = "Keperluan wajib diisi"
            binding.etKeperluan.requestFocus()
            return false
        }

        val format = SimpleDateFormat(
            "dd MMMM yyyy",
            Locale("id", "ID")
        ).apply {
            isLenient = false
        }

        val tanggalDipilih = try {
            format.parse(tanggal)
        } catch (_: Exception) {
            null
        }

        if (tanggalDipilih == null) {
            Toast.makeText(
                this,
                "Tanggal tidak valid. Silakan pilih ulang.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        val hariIni = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val tanggalKalender = Calendar.getInstance().apply {
            time = tanggalDipilih
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (tanggalKalender.before(hariIni)) {
            Toast.makeText(
                this,
                "Tanggal peminjaman tidak boleh di masa lalu",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        binding.etTanggal.error = null
        binding.etWaktuMulai.error = null
        binding.etWaktuSelesai.error = null
        binding.etKeperluan.error = null

        return true
    }

    private fun validateStepThree(): Boolean {
        if (selectedBorangUri == null) {
            binding.tvStatusBorang.text =
                "Pilih borang resmi kampus sebelum melanjutkan"

            Toast.makeText(
                this,
                "Borang peminjaman wajib dilampirkan",
                Toast.LENGTH_SHORT
            ).show()

            return false
        }

        return true
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply {
                    set(year, month, day)
                }

                val format = SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale("id", "ID")
                )

                binding.etTanggal.setText(format.format(selected.time))
                binding.etTanggal.error = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun showTimePicker(target: EditText) {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, hour, minute ->
                target.setText(
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hour,
                        minute
                    )
                )
                target.error = null
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateReview() {
        binding.tvReviewBarang.text =
            binding.etNamaBarang.text.toString().trim()

        binding.tvReviewJumlah.text =
            "${binding.etJumlah.text.toString().trim()} unit"

        binding.tvReviewKeterangan.text =
            binding.etKeterangan.text.toString().trim()
                .ifEmpty { "Tidak ada keterangan" }

        binding.tvReviewTanggal.text =
            binding.etTanggal.text.toString()

        binding.tvReviewWaktu.text =
            "${binding.etWaktuMulai.text} - ${binding.etWaktuSelesai.text}"

        binding.tvReviewKeperluan.text =
            binding.etKeperluan.text.toString().trim()

        binding.tvReviewBorang.text = selectedBorangName
    }

    private fun getFileName(uri: Uri): String {
        var fileName: String? = null

        if (uri.scheme == "content") {
            val cursor = contentResolver.query(
                uri,
                arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(
                        android.provider.OpenableColumns.DISPLAY_NAME
                    )

                    if (index >= 0) {
                        fileName = it.getString(index)
                    }
                }
            }
        }

        if (fileName.isNullOrBlank()) {
            fileName = uri.lastPathSegment
        }

        return fileName ?: "Borang_peminjaman"
    }

    private fun saveSubmission() {
        val borangUri = selectedBorangUri

        if (borangUri == null) {
            currentStep = 3
            showStep()
            validateStepThree()
            return
        }

        val preferences = getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )

        val existingJson = preferences.getString(
            submissionsKey,
            "[]"
        ) ?: "[]"

        val submissions = try {
            JSONArray(existingJson)
        } catch (_: Exception) {
            JSONArray()
        }

        val submissionId = "BRG-${System.currentTimeMillis()}"

        val submission = JSONObject().apply {
            put("id", submissionId)
            put("kategori", "Barang")
            put(
                "namaBarang",
                binding.etNamaBarang.text.toString().trim()
            )
            put(
                "jumlah",
                binding.etJumlah.text.toString().trim().toInt()
            )
            put(
                "keterangan",
                binding.etKeterangan.text.toString().trim()
            )
            put("tanggal", binding.etTanggal.text.toString())
            put("waktuMulai", binding.etWaktuMulai.text.toString())
            put("waktuSelesai", binding.etWaktuSelesai.text.toString())
            put(
                "keperluan",
                binding.etKeperluan.text.toString().trim()
            )
            put("borangNama", selectedBorangName)
            put("borangUri", borangUri.toString())
            put("status", "Menunggu")
            put(
                "dibuatPada",
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())
            )
        }

        submissions.put(submission)

        val saved = preferences.edit()
            .putString(submissionsKey, submissions.toString())
            .commit()

        if (saved) {
            AlertDialog.Builder(this)
                .setTitle("Pengajuan Berhasil")
                .setMessage(
                    "Pengajuan peminjaman barang berhasil disimpan.\n\n" +
                            "ID: $submissionId\n" +
                            "Borang: $selectedBorangName\n" +
                            "Status: Menunggu\n\n" +
                            "Data pengajuan tersimpan secara lokal. " +
                            "Berkas borang tetap berada di lokasi asalnya."
                )
                .setCancelable(false)
                .setPositiveButton("Selesai") { _, _ ->
                    finish()
                }
                .show()
        } else {
            Toast.makeText(
                this,
                "Pengajuan gagal disimpan. Silakan coba lagi.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showCancelDialog() {
        AlertDialog.Builder(this)
            .setTitle("Batalkan Pengajuan?")
            .setMessage("Data yang sudah diisi tidak akan dikirim.")
            .setNegativeButton("Lanjut Mengisi", null)
            .setPositiveButton("Batalkan") { _, _ ->
                finish()
            }
            .show()
    }
}