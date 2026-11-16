
package com.example.nai_taro.Riwayat

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RiwayatTabsAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    private val daftarStatus = listOf(
        "Semua",
        "Menunggu",
        "Disetujui",
        "Dipinjam",
        "Dikembalikan"
    )

    override fun getItemCount(): Int = daftarStatus.size

    override fun createFragment(position: Int): Fragment {
        return RiwayatTabFragment.newInstance(
            daftarStatus[position]
        )
    }
}
