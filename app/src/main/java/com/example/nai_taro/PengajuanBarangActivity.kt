
package com.example.nai_taro

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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

    private var selectedDate = ""
    private var selectedStartTime = ""
    private var selectedEndTime = ""
    private var selectedDocumentUri: Uri? = null
    private var selectedDocumentName = ""

    private val documentPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) {
                    // Beberapa penyedia dokumen tidak mendukung izin permanen.
                }

                selectedDocumentUri = uri
                selectedDocumentName = getFileName(uri)
                binding.tvNamaDokumen.text = selectedDocumentName
                binding.btnPilihDokumen.text = "Ganti dokumen"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPengajuanBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kategori = listOf(
            "Barang",
            "Ruang",
            "Laboratorium",
            "Aset / Inventaris",
            "Lainnya"
        )

        binding.spinnerKategori.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            kategori
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.btnTanggal.setOnClickListener {
            showDatePicker()
        }

        binding.btnJamMulai.setOnClickListener {
            showTimePicker(true)
        }

        binding.btnJamSelesai.setOnClickListener {
            showTimePicker(false)
        }

        binding.btnPilihDokumen.setOnClickListener {
            documentPicker.launch(
                arrayOf("application/pdf", "image/jpeg", "image/png")
            )
        }

        binding.btnKembali.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                showStep()
            } else {
                showCancelDialog()
            }
        }

        binding.btnLanjut.setOnClickListener {
            when (currentStep) {
                1 -> if (validateStepOne()) {
                    currentStep = 2
                    showStep()
                }

                2 -> if (validateStepTwo()) {
                    currentStep = 3
                    showStep()
                }

                3 -> {
                    updateReview()
                    currentStep = 4
                    showStep()
                }

                4 -> saveSubmission()
            }
        }

        showStep()
    }

    private fun showStep() {
        binding.step1.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        binding.step2.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        binding.step3.visibility = if (currentStep == 3) View.VISIBLE else View.GONE
        binding.step4.visibility = if (currentStep == 4) View.VISIBLE else View.GONE

        binding.progressWizard.max = totalSteps
        binding.progressWizard.progress = currentStep
        binding.tvStep.text = "Langkah $currentStep dari $totalSteps"

        binding.btnKembali.text = if (currentStep == 1) "Batal" else "Kembali"
        binding.btnLanjut.text =
            if (currentStep == totalSteps) "Kirim Pengajuan" else "Lanjut"
    }

    private fun validateStepOne(): Boolean {
        if (binding.etNamaAset.text.toString().trim().isEmpty()) {
            binding.etNamaAset.error = "Nama barang atau fasilitas wajib diisi"
            binding.etNamaAset.requestFocus()
            return false
        }

        return true
    }

    private fun validateStepTwo(): Boolean {
        if (selectedDate.isEmpty()) {
            toast("Silakan pilih tanggal peminjaman")
            return false
        }

        if (selectedStartTime.isEmpty()) {
            toast("Silakan pilih jam mulai")
            return false
        }

        if (selectedEndTime.isEmpty()) {
            toast("Silakan pilih jam selesai")
            return false
        }

        if (selectedEndTime <= selectedStartTime) {
            toast("Jam selesai harus setelah jam mulai")
            return false
        }

        if (binding.etTujuan.text.toString().trim().isEmpty()) {
            binding.etTujuan.error = "Tujuan peminjaman wajib diisi"
            binding.etTujuan.requestFocus()
            return false
        }

        return true
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = Calendar.getInstance().apply {
                    set(year, month, day, 12, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                selectedDate = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).format(date.time)

                val displayDate = SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale("id", "ID")
                ).format(date.time)

                binding.btnTanggal.text = displayDate
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
        }.show()
    }

    private fun showTimePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, hour, minute ->
                val time = String.format(
                    Locale.US,
                    "%02d:%02d",
                    hour,
                    minute
                )

                if (isStart) {
                    selectedStartTime = time
                    binding.btnJamMulai.text = "Jam mulai: $time"
                } else {
                    selectedEndTime = time
                    binding.btnJamSelesai.text = "Jam selesai: $time"
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateReview() {
        val kategori = binding.spinnerKategori.selectedItem.toString()
        val namaAset = binding.etNamaAset.text.toString().trim()
        val tujuan = binding.etTujuan.text.toString().trim()
        val tanggalDisplay = if (selectedDate.isNotEmpty()) {
            try {
                val date = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).parse(selectedDate)

                SimpleDateFormat(
                    "dd MMMM yyyy",
                    Locale("id", "ID")
                ).format(date ?: Date())
            } catch (_: Exception) {
                selectedDate
            }
        } else {
            "-"
        }

        binding.tvRingkasan.text = """
            Kategori: $kategori
            Nama barang/fasilitas: $namaAset
            Tanggal: $tanggalDisplay
            Waktu: $selectedStartTime - $selectedEndTime
            Tujuan: $tujuan
            Dokumen: ${selectedDocumentName.ifEmpty { "Tidak dilampirkan" }}
        """.trimIndent()
    }

    private fun saveSubmission() {
        if (!validateStepOne() || !validateStepTwo()) {
            currentStep = if (selectedDate.isEmpty() ||
                selectedStartTime.isEmpty() ||
                selectedEndTime.isEmpty()
            ) 2 else 1
            showStep()
            return
        }

        val preferences = getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )

        val submissions = try {
            JSONArray(
                preferences.getString(submissionsKey, "[]") ?: "[]"
            )
        } catch (_: Exception) {
            JSONArray()
        }

        val submissionId = "SIGAP-${System.currentTimeMillis()}"
        val kategori = binding.spinnerKategori.selectedItem.toString()
        val namaAset = binding.etNamaAset.text.toString().trim()
        val tujuan = binding.etTujuan.text.toString().trim()

        val submission = JSONObject().apply {
            put("id", submissionId)
            put("kategori", kategori)
            put("namaAset", namaAset)
            put("namaBarang", namaAset)
            put("tanggal", selectedDate)
            put("jamMulai", selectedStartTime)
            put("jamSelesai", selectedEndTime)
            put("waktuMulai", selectedStartTime)
            put("waktuSelesai", selectedEndTime)
            put("tujuan", tujuan)
            put("keperluan", tujuan)
            put("dokumenNama", selectedDocumentName)
            put("dokumenUri", selectedDocumentUri?.toString() ?: "")
            put("status", "Menunggu")
            put(
                "dibuatPada",
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.US
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
                    "Pengajuan berhasil disimpan.\n\n" +
                            "ID: $submissionId\n" +
                            "Kategori: $kategori\n" +
                            "Nama: $namaAset\n" +
                            "Status: Menunggu"
                )
                .setCancelable(false)
                .setPositiveButton("Selesai") { _, _ ->
                    finish()
                }
                .show()
        } else {
            toast("Pengajuan gagal disimpan. Silakan coba lagi.")
        }
    }

    private fun getFileName(uri: Uri): String {
        var name: String? = null

        if (uri.scheme == "content") {
            contentResolver.query(
                uri,
                arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(
                        android.provider.OpenableColumns.DISPLAY_NAME
                    )
                    if (index >= 0) {
                        name = cursor.getString(index)
                    }
                }
            }
        }

        return name ?: uri.lastPathSegment ?: "Dokumen"
    }

    private fun showCancelDialog() {
        AlertDialog.Builder(this)
            .setTitle("Batalkan Pengajuan?")
            .setMessage("Data yang sudah diisi tidak akan disimpan.")
            .setNegativeButton("Lanjut Mengisi", null)
            .setPositiveButton("Batalkan") { _, _ ->
                finish()
            }
            .show()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
