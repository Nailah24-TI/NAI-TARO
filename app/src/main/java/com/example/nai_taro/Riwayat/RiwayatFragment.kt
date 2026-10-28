
package com.example.nai_taro.Riwayat

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.nai_taro.DetailPeminjamanActivity
import com.example.nai_taro.R
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray

class RiwayatFragment : Fragment() {

    private val preferencesName = "SIGAP_PREF"
    private val submissionsKey = "pengajuan_barang_list"

    private lateinit var containerRiwayat: LinearLayout

    private val navy = Color.rgb(15, 67, 145)
    private val blue = Color.rgb(22, 101, 216)
    private val gray = Color.rgb(100, 116, 139)
    private val lightGray = Color.rgb(226, 234, 245)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerRiwayat = view.findViewById(R.id.containerRiwayat)
        tampilkanRiwayat()
    }

    override fun onResume() {
        super.onResume()
        if (::containerRiwayat.isInitialized) tampilkanRiwayat()
    }

    private fun tampilkanRiwayat() {
        containerRiwayat.removeAllViews()

        val prefs = requireContext().getSharedPreferences(
            preferencesName,
            Context.MODE_PRIVATE
        )
        val data = JSONArray(prefs.getString(submissionsKey, "[]") ?: "[]")

        if (data.length() == 0) {
            val empty = TextView(requireContext()).apply {
                text = "Belum ada pengajuan.\nPengajuan yang kamu buat akan muncul di sini."
                textSize = 14f
                setTextColor(gray)
                gravity = Gravity.CENTER
                setPadding(24, 32, 24, 32)
                background = roundedBackground(Color.WHITE, lightGray, 16f)
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

        for (i in data.length() - 1 downTo 0) {
            val item = data.optJSONObject(i) ?: continue

            val id = item.optString("id")
            val kategori = item.optString("kategori", "Pengajuan")
            val nama = item.optString("namaAset")
                .ifBlank { item.optString("namaBarang", "Aset") }
            val tanggal = item.optString("tanggal", "-")
            val status = item.optString("status", "Menunggu")

            containerRiwayat.addView(
                buatKartu(id, kategori, nama, tanggal, status)
            )
        }
    }

    private fun buatKartu(
        id: String,
        kategori: String,
        nama: String,
        tanggal: String,
        status: String
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
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        content.addView(topRow)

        val dateText = TextView(context).apply {
            text = "Tanggal  ·  $tanggal"
            textSize = 13f
            setTextColor(gray)
            setPadding(0, dp(14), 0, dp(12))
        }
        content.addView(dateText)

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
        bottomRow.addView(
            statusBadge,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

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
        card.addView(content)

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

    private fun statusColor(status: String): Int {
        return when (status.lowercase()) {
            "menunggu" -> Color.rgb(161, 98, 7)
            "disetujui", "dipinjam" -> Color.rgb(21, 101, 192)
            "ditolak" -> Color.rgb(185, 28, 28)
            "dikembalikan", "selesai" -> Color.rgb(21, 128, 61)
            else -> gray
        }
    }

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
