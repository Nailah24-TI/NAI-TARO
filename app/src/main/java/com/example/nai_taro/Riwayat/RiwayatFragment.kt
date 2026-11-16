
package com.example.nai_taro.Riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.nai_taro.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RiwayatFragment : Fragment() {

    private var tabMediator: TabLayoutMediator? = null

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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(
            R.id.tabLayoutRiwayat
        )

        val viewPager = view.findViewById<ViewPager2>(
            R.id.viewPagerRiwayat
        )

        viewPager.adapter = RiwayatTabsAdapter(this)

        tabMediator = TabLayoutMediator(
            tabLayout,
            viewPager
        ) { tab, position ->

            when (position) {
                0 -> tab.text = "Semua"
                1 -> tab.text = "Menunggu"
                2 -> tab.text = "Disetujui"
                3 -> tab.text = "Dipinjam"
                4 -> tab.text = "Dikembalikan"
            }
        }

        tabMediator?.attach()
    }

    override fun onDestroyView() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroyView()
    }
}
