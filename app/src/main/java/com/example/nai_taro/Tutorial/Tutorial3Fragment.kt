
package com.example.nai_taro.Tutorial

import android.graphics.Color
import com.example.nai_taro.Tutorial.TutorialBaseFragment

class Tutorial3Fragment : TutorialBaseFragment() {

    override fun getIcon(): String = "📱"

    override fun getTitleText(): String {
        return "Pantau Pengajuanmu"
    }

    override fun getDescription(): String {
        return "Lihat riwayat peminjaman, informasi persetujuan, bukti pengembalian, serta berikan rating dan ulasan melalui SIGAP."
    }

    override fun getBackgroundColor(): Int {
        return Color.rgb(10, 48, 105)
    }
}
