
package com.example.nai_taro.Tutorial

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

abstract class TutorialBaseFragment : Fragment() {

    abstract fun getIcon(): String
    abstract fun getTitleText(): String
    abstract fun getDescription(): String
    abstract fun getBackgroundColor(): Int

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val context = requireContext()

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(30), dp(28), dp(30))
            setBackgroundColor(getBackgroundColor())
        }

        val logo = TextView(context).apply {
            text = getIcon()
            textSize = 76f
            gravity = Gravity.CENTER
        }

        root.addView(
            logo,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(150)
            )
        )

        val appName = TextView(context).apply {
            text = "SIGAP"
            textSize = 24f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val appNameParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(12)
        }

        root.addView(appName, appNameParams)

        val title = TextView(context).apply {
            text = getTitleText()
            textSize = 21f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val titleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(30)
        }

        root.addView(title, titleParams)

        val description = TextView(context).apply {
            text = getDescription()
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setLineSpacing(dp(5).toFloat(), 1f)
        }

        val descriptionParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(16)
        }

        root.addView(description, descriptionParams)

        return root
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
