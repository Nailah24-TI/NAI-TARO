
package com.example.nai_taro.Riwayat

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.nai_taro.DetailPeminjamanActivity
import com.example.nai_taro.R
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class RiwayatFragment : Fragment() {

    private val preferencesName = "SIGAP_PREF"
    private val submissionsKey = "pengajuan_barang_list"
    private val reviewsKey = "rating_ulasan_list"

    private lateinit var containerRiwayat: LinearLayout

    private val navy = Color.rgb(15, 67, 145)
    private val blue = Color.rgb(22, 101, 216)
    private val gray = Color.rgb(100, 116, 139)
    private val lightGray = Color.rgb(226, 234, 245)
    private val gold = Color.rgb(217, 153, 22)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_riwayat,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        containerRiwayat = view.findViewById(R.id.containerRiwayat)

        tampilkanRiwayat()
    }

    override fun onResume() {
        super.onResume()

        if (::containerRiwayat.isInitialized) {
            tampilkanRiwayat()
        }
    }

    // Menampilkan seluruh riwayat pengajuan
    private fun tampilkanRiwayat() {

        containerRiwayat.removeAllViews()

        val prefs = requireContext().getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )

        val data = JSONArray(
            prefs.getString(submissionsKey, "[]") ?: "[]"
        )

        val reviews = JSONArray(
            prefs.getString(reviewsKey, "[]") ?: "[]"
        )

        if (data.length() == 0) {

            val empty = TextView(requireContext()).apply {
                text = "Belum ada pengajuan.\nPengajuan yang kamu buat akan muncul di sini."
                textSize = 14f
                setTextColor(gray)
                gravity = Gravity.CENTER
                setPadding(24, 32, 24, 32)
                background = roundedBackground(
                    Color.WHITE,
                    lightGray,
                    16f
                )
            }

            containerRiwayat.addView(
                empty,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )

            return
        }

        // Menampilkan pengajuan terbaru di bagian paling atas
        for (i in data.length() - 1 downTo 0) {

            val item = data.optJSONObject(i) ?: continue

            val id = item.optString("id")
            val kategori = item.optString("kategori", "Pengajuan")

            val nama = item.optString("namaAset")
                .ifBlank {
                    item.optString("namaBarang", "Aset")
                }

            val tanggal = item.optString("tanggal", "-")
            val status = item.optString("status", "Menunggu")

            // Mencari ulasan berdasarkan ID pengajuan
            val review = cariUlasan(reviews, id)

            containerRiwayat.addView(
                buatKartu(
                    id = id,
                    kategori = kategori,
                    nama = nama,
                    tanggal = tanggal,
                    status = status,
                    fotoBuktiPath = item.optString("fotoBuktiPengembalian"),
                    review = review
                )
            )
        }
    }

    // Mencari rating dan ulasan berdasarkan submissionId
    private fun cariUlasan(
        reviews: JSONArray,
        submissionId: String
    ): JSONObject? {

        for (i in 0 until reviews.length()) {

            val review = reviews.optJSONObject(i) ?: continue

            if (review.optString("submissionId") == submissionId) {
                return review
            }
        }

        return null
    }

    // Membuat kartu riwayat
    private fun buatKartu(
        id: String,
        kategori: String,
        nama: String,
        tanggal: String,
        status: String,
        fotoBuktiPath: String,
        review: JSONObject?
    ): View {

        val context = requireContext()

        val card = MaterialCardView(context).apply {
            radius = dp(16).toFloat()
            cardElevation = dp(1).toFloat()
            setCardBackgroundColor(Color.WHITE)
            strokeWidth = dp(1)
            setStrokeColor(lightGray)
            isClickable = true
            isFocusable = true
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(12)
        }

        card.layoutParams = params

        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        // Bagian atas kartu
        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox = TextView(context).apply {
            text = "▤"
            textSize = 23f
            gravity = Gravity.CENTER
            setTextColor(navy)

            background = roundedBackground(
                Color.rgb(234, 241, 252),
                Color.TRANSPARENT,
                12f
            )
        }

        topRow.addView(
            iconBox,
            LinearLayout.LayoutParams(dp(46), dp(46))
        )

        val titleColumn = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }

        val categoryText = TextView(context).apply {
            text = kategori
            textSize = 12f
            setTextColor(gray)
        }

        val nameText = TextView(context).apply {
            text = nama
            textSize = 16f
            setTextColor(navy)
            setTypeface(null, Typeface.BOLD)
            maxLines = 2
        }

        titleColumn.addView(categoryText)
        titleColumn.addView(nameText)

        topRow.addView(
            titleColumn,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        content.addView(topRow)

        // Tanggal pengajuan
        val dateText = TextView(context).apply {
            text = "Tanggal  ·  $tanggal"
            textSize = 13f
            setTextColor(gray)
            setPadding(0, dp(14), 0, dp(12))
        }

        content.addView(dateText)

        // Baris status
        val bottomRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val statusBadge = TextView(context).apply {
            text = status
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(statusColor(status))
            setPadding(dp(10), dp(7), dp(10), dp(7))

            background = roundedBackground(
                statusBackground(status),
                Color.TRANSPARENT,
                20f
            )
        }

        bottomRow.addView(statusBadge)

        val spacer = View(context)

        bottomRow.addView(
            spacer,
            LinearLayout.LayoutParams(0, 1, 1f)
        )

        val detailText = TextView(context).apply {
            text = "Lihat detail  ›"
            textSize = 13f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
        }

        bottomRow.addView(detailText)

        content.addView(bottomRow)

        // Garis pemisah
        val divider = View(context).apply {
            setBackgroundColor(lightGray)
        }

        val dividerParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(1)
        ).apply {
            topMargin = dp(14)
            bottomMargin = dp(14)
        }

        content.addView(divider, dividerParams)

        // Bagian bukti pengembalian
        val proofTitle = TextView(context).apply {
            text = "Bukti Pengembalian"
            textSize = 14f
            setTextColor(navy)
            setTypeface(null, Typeface.BOLD)
        }

        content.addView(proofTitle)

        val fotoFile = File(fotoBuktiPath)

        if (status.equals("Dikembalikan", ignoreCase = true) &&
            fotoBuktiPath.isNotBlank() &&
            fotoFile.exists()
        ) {

            val preview = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = "Foto bukti pengembalian"
                setImageBitmap(
                    BitmapFactory.decodeFile(fotoFile.absolutePath)
                )
                background = roundedBackground(
                    Color.rgb(232, 237, 245),
                    Color.TRANSPARENT,
                    12f
                )
            }

            val previewParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(170)
            ).apply {
                topMargin = dp(10)
            }

            content.addView(preview, previewParams)

        } else {

            val noProof = TextView(context).apply {
                text = if (status == "Dikembalikan") {
                    "Foto bukti tidak ditemukan."
                } else {
                    "Belum ada foto bukti pengembalian."
                }

                textSize = 12f
                setTextColor(gray)
                setPadding(dp(12), dp(12), dp(12), dp(12))

                background = roundedBackground(
                    Color.rgb(245, 247, 251),
                    Color.TRANSPARENT,
                    10f
                )
            }

            val noProofParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }

            content.addView(noProof, noProofParams)
        }

        // Bagian rating dan ulasan
        val ratingDivider = View(context).apply {
            setBackgroundColor(lightGray)
        }

        val ratingDividerParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(1)
        ).apply {
            topMargin = dp(14)
            bottomMargin = dp(12)
        }

        content.addView(ratingDivider, ratingDividerParams)

        val ratingTitle = TextView(context).apply {
            text = "Rating & Ulasan"
            textSize = 14f
            setTextColor(navy)
            setTypeface(null, Typeface.BOLD)
        }

        content.addView(ratingTitle)

        if (review != null) {

            // Mengambil nilai rating dari data ulasan
            val nilaiRating = review.optInt("rating", 0)
                .coerceIn(0, 5)

            val ratingRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            val stars = TextView(context).apply {
                text = buildString {
                    repeat(nilaiRating) {
                        append("★")
                    }

                    repeat(5 - nilaiRating) {
                        append("☆")
                    }
                }

                textSize = 22f
                setTextColor(gold)
            }

            ratingRow.addView(stars)

            val ratingNumber = TextView(context).apply {
                text = "  $nilaiRating/5"
                textSize = 13f
                setTextColor(gray)
                setTypeface(null, Typeface.BOLD)
            }

            ratingRow.addView(ratingNumber)

            val ratingParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(6)
            }

            content.addView(ratingRow, ratingParams)

            // Menampilkan ulasan pengguna
            val ulasan = review.optString("ulasan", "")

            if (ulasan.isNotBlank()) {

                val reviewText = TextView(context).apply {
                    text = "\"$ulasan\""
                    textSize = 13f
                    setTextColor(gray)
                    setPadding(0, dp(6), 0, 0)
                }

                content.addView(reviewText)
            }

        } else {

            val noRating = TextView(context).apply {
                text = "Belum diberi rating"
                textSize = 12f
                setTextColor(gray)
                setPadding(0, dp(6), 0, 0)
            }

            content.addView(noRating)
        }

        card.addView(content)

        // Klik kartu membuka detail peminjaman
        card.setOnClickListener {

            val intent = Intent(
                requireContext(),
                DetailPeminjamanActivity::class.java
            ).putExtra(
                DetailPeminjamanActivity.EXTRA_SUBMISSION_ID,
                id
            )

            startActivity(intent)
        }

        return card
    }

    // Background dengan sudut membulat
    private fun roundedBackground(
        color: Int,
        stroke: Int,
        radiusDp: Float
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp.toInt()).toFloat()

            if (stroke != Color.TRANSPARENT) {
                setStroke(dp(1), stroke)
            }
        }
    }

    // Warna teks status
    private fun statusColor(status: String): Int {

        return when (status.lowercase()) {
            "menunggu" -> Color.rgb(161, 98, 7)
            "disetujui", "dipinjam" -> Color.rgb(21, 101, 192)
            "ditolak" -> Color.rgb(185, 28, 28)
            "dikembalikan", "selesai" -> Color.rgb(21, 128, 61)
            else -> gray
        }
    }

    // Warna latar status
    private fun statusBackground(status: String): Int {

        return when (status.lowercase()) {
            "menunggu" -> Color.rgb(254, 243, 199)
            "disetujui", "dipinjam" -> Color.rgb(219, 234, 254)
            "ditolak" -> Color.rgb(254, 226, 226)
            "dikembalikan", "selesai" -> Color.rgb(220, 252, 231)
            else -> Color.rgb(241, 245, 249)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
