
package com.example.nai_taro.Tutorial

import android.graphics.Color
import com.example.nai_taro.Tutorial.TutorialBaseFragment

class Tutorial1Fragment : TutorialBaseFragment() {

    override fun getIcon(): String = "🏫"

    override fun getTitleText(): String {
        return "Selamat Datang di SIGAP"
    }

    override fun getDescription(): String {
        return "SIGAP merupakan aplikasi layanan sarana dan prasarana kampus PCR yang membantu proses pengajuan peminjaman menjadi lebih mudah."
    }

    override fun getBackgroundColor(): Int {
        return Color.rgb(15, 67, 145)
    }
}
