package pion.tech.pionbase.feature.setting

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import pion.tech.pionbase.BuildConfig
import pion.tech.pionbase.R
import pion.tech.pionbase.util.setPreventDoubleClickScaleView

fun SettingFragment.initView() {
    val remoteResult = if (pion.tech.pionbase.util.AppRemoteConfig.isRemoteConfigSuccess) "R" else "D"
    binding.txvVersion.text = buildString {
        append("Application version: v")
        append(" ")
        append(remoteResult)
        append(" ")
        append(BuildConfig.VERSION_CODE)
        append(" ")
        append(BuildConfig.VERSION_NAME)
    }
    setupColorCircles()
}

fun SettingFragment.applyEvent() {
    binding.switchCalendar.setOnCheckedChangeListener { _, isChecked ->
        viewModel.toggleCalendarOverlay(requireContext(), isChecked)
    }

    binding.btnPosTop.setPreventDoubleClickScaleView { viewModel.setCalendarPosition(requireContext(), 0) }
    binding.btnPosCenter.setPreventDoubleClickScaleView { viewModel.setCalendarPosition(requireContext(), 1) }
    binding.btnPosBottom.setPreventDoubleClickScaleView { viewModel.setCalendarPosition(requireContext(), 2) }

    binding.btnPickMedia.setPreventDoubleClickScaleView {
        // Existing media pick logic
    }
}

fun SettingFragment.onBackEvent() {
    onSystemBack { navigator.navigateUp() }
}

fun SettingFragment.updateCalendarOverlayUI(isEnabled: Boolean) {
    binding.switchCalendar.isChecked = isEnabled
    binding.tvCalendarStatus.text = if (isEnabled) getString(R.string.on) else getString(R.string.off)
}

fun SettingFragment.updatePositionUI(position: Int) {
    val selectedBg = R.drawable.bg_category_selected
    val unselectedBg = R.drawable.bg_category_unselected
    val selectedColor = ContextCompat.getColor(requireContext(), R.color.black)
    val unselectedColor = ContextCompat.getColor(requireContext(), R.color.white)

    binding.btnPosTop.apply {
        setBackgroundResource(if (position == 0) selectedBg else unselectedBg)
        setTextColor(if (position == 0) selectedColor else unselectedColor)
    }
    binding.btnPosCenter.apply {
        setBackgroundResource(if (position == 1) selectedBg else unselectedBg)
        setTextColor(if (position == 1) selectedColor else unselectedColor)
    }
    binding.btnPosBottom.apply {
        setBackgroundResource(if (position == 2) selectedBg else unselectedBg)
        setTextColor(if (position == 2) selectedColor else unselectedColor)
    }
}

fun SettingFragment.updateColorUI(selectedColor: Int) {
    val layout = binding.layoutColors
    val strokeWidth = getDimenPixelSize("_2dp")
    for (i in 0 until layout.childCount) {
        val view = layout.getChildAt(i) as? ImageView ?: continue
        val color = view.tag as? Int ?: continue
        
        // Show border if selected
        val drawable = view.background as? GradientDrawable ?: continue
        if (color == selectedColor) {
            drawable.setStroke(strokeWidth, Color.WHITE)
        } else {
            drawable.setStroke(0, Color.TRANSPARENT)
        }
    }
}

private fun SettingFragment.setupColorCircles() {
    val colors = listOf(
        Color.WHITE,
        Color.parseColor("#E199FF"), // Purple
        Color.parseColor("#80DEEA"), // Cyan
        Color.parseColor("#FFAB91"), // Orange
        Color.parseColor("#FFF59D")  // Yellow
    )

    val layout = binding.layoutColors
    layout.removeAllViews()

    val size = getDimenPixelSize("_32dp")
    val margin = getDimenPixelSize("_8dp")

    colors.forEach { color ->
        val imageView = ImageView(requireContext()).apply {
            tag = color
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                marginStart = margin
                marginEnd = margin
            }
            
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
            background = drawable
            
            setPreventDoubleClickScaleView {
                viewModel.setCalendarFontColor(requireContext(), color)
            }
        }
        layout.addView(imageView)
    }
}

private fun SettingFragment.getDimenPixelSize(name: String): Int {
    val id = resources.getIdentifier(name, "dimen", context?.packageName)
    return if (id != 0) resources.getDimensionPixelSize(id) else 0
}
