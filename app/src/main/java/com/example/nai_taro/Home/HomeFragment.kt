
package com.example.nai_taro.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.nai_taro.PengajuanBarangActivity
import com.example.nai_taro.R
import com.example.nai_taro.WebActivity

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

        view.findViewById<View>(R.id.cardBarang)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        PengajuanBarangActivity::class.java
                    )
                )
            }

        view.findViewById<View>(R.id.btnWebsite)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        WebActivity::class.java
                    )
                )
            }

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

        view.findViewById<View>(R.id.tvSeeAll)
            .setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Halaman riwayat akan tersedia di menu Message.",
                    Toast.LENGTH_SHORT
                ).show()
            }

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
    }
}
