
package com.example.nai_taro.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.nai_taro.PengajuanBarangActivity
import com.example.nai_taro.R
import com.example.nai_taro.WebActivity
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_home,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Membuka form pengajuan
        view.findViewById<View>(R.id.cardBarang)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        PengajuanBarangActivity::class.java
                    )
                )
            }

        // Membuka website PCR
        view.findViewById<View>(R.id.btnWebsite)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        WebActivity::class.java
                    )
                )
            }

        // Layanan yang belum tersedia
        val layananBelumTersedia = View.OnClickListener {
            Toast.makeText(
                requireContext(),
                "Layanan ini belum tersedia.",
                Toast.LENGTH_SHORT
            ).show()
        }

        view.findViewById<View>(R.id.cardRuang)
            .setOnClickListener(layananBelumTersedia)

        view.findViewById<View>(R.id.cardLab)
            .setOnClickListener(layananBelumTersedia)

        view.findViewById<View>(R.id.cardAset)
            .setOnClickListener(layananBelumTersedia)

        view.findViewById<View>(R.id.cardLainnya)
            .setOnClickListener(layananBelumTersedia)

        // Tombol lihat semua riwayat
        view.findViewById<View>(R.id.tvSeeAll)
            .setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Halaman riwayat akan tersedia di menu Message.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Detail contoh pengajuan di dashboard
        view.findViewById<View>(R.id.cardStatus)
            .setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Detail Pengajuan")
                    .setMessage(
                        "Ruang Lab Komputer 2\nStatus: Menunggu\n\n" +
                                "Ini merupakan contoh data untuk tampilan dashboard."
                    )
                    .setPositiveButton("Tutup", null)
                    .show()
            }

        // Menyiapkan kalender peminjaman
        setupKalender(view)
    }

    private fun setupKalender(view: View) {
        val calendarView =
            view.findViewById<CalendarView>(R.id.calendarPeminjaman)

        val tvTanggal =
            view.findViewById<TextView>(R.id.tvTanggalKalender)

        val tvJadwal =
            view.findViewById<TextView>(R.id.tvJadwalKalender)

        val formatTanggal = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        )

        val formatTampilan = SimpleDateFormat(
            "EEEE, dd MMMM yyyy",
            Locale("id", "ID")
        )

        fun tampilkanJadwal(millis: Long) {
            val tanggalDipilih = formatTanggal.format(Date(millis))

            tvTanggal.text = formatTampilan.format(Date(millis))

            val preferences = requireContext().getSharedPreferences(
                "SIGAP_PREF",
                Context.MODE_PRIVATE
            )

            val jsonString = preferences.getString(
                "pengajuan_barang_list",
                "[]"
            ) ?: "[]"

            val daftarJadwal = mutableListOf<String>()

            try {
                val data = JSONArray(jsonString)

                for (i in 0 until data.length()) {
                    val item = data.optJSONObject(i) ?: continue

                    if (item.optString("tanggal") != tanggalDipilih) {
                        continue
                    }

                    val kategori = item.optString("kategori", "Aset")
                    val namaAset = item.optString("namaAset", "-")
                    val jamMulai = item.optString("jamMulai", "-")
                    val jamSelesai = item.optString("jamSelesai", "-")
                    val status = item.optString("status", "Menunggu")

                    daftarJadwal.add(
                        "• $kategori: $namaAset\n" +
                                "  Waktu: $jamMulai - $jamSelesai\n" +
                                "  Status: $status"
                    )
                }

                tvJadwal.text = if (daftarJadwal.isEmpty()) {
                    "Belum ada pengajuan pada tanggal ini."
                } else {
                    daftarJadwal.joinToString("\n\n")
                }
            } catch (_: Exception) {
                tvJadwal.text =
                    "Data pengajuan lokal tidak dapat dibaca."
            }
        }

        calendarView.setOnDateChangeListener {
                _, year, month, day ->

            val tanggal = Calendar.getInstance().apply {
                set(year, month, day, 12, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            tampilkanJadwal(tanggal.timeInMillis)
        }

        // Tampilkan jadwal untuk tanggal yang sedang dipilih
        tampilkanJadwal(calendarView.date)
    }

    override fun onResume() {
        super.onResume()

        // Refresh kalender ketika kembali dari form pengajuan
        view?.let { setupKalender(it) }
    }
}
