
package com.example.nai_taro.More

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.nai_taro.AuthActivity
import com.example.nai_taro.DeveloperActivity
import com.example.nai_taro.R
import com.example.nai_taro.WebActivity

class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_more,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnDeveloper)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        DeveloperActivity::class.java
                    )
                )
            }

        view.findViewById<View>(R.id.btnWebsiteMore)
            .setOnClickListener {
                startActivity(
                    Intent(
                        requireContext(),
                        WebActivity::class.java
                    )
                )
            }

        view.findViewById<View>(R.id.btnLogout)
            .setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Apakah Anda yakin ingin logout?")
                    .setNegativeButton("Tidak", null)
                    .setPositiveButton("Ya") { _, _ ->
                        requireContext()
                            .getSharedPreferences(
                                "SIGAP_PREF",
                                android.content.Context.MODE_PRIVATE
                            )
                            .edit()
                            .putBoolean("isLogin", false)
                            .apply()

                        startActivity(
                            Intent(
                                requireContext(),
                                AuthActivity::class.java
                            )
                        )

                        requireActivity().finish()
                    }
                    .show()
            }
    }
}
