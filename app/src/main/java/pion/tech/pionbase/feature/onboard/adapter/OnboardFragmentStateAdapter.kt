package pion.tech.pionbase.feature.onboard.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen1Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen2Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen3Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen4Fragment

enum class OnboardScreen {
    SCREEN_1, SCREEN_2, SCREEN_3, SCREEN_4
}

class OnboardFragmentStateAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {

    private val screens = buildList {
        add(OnboardScreen.SCREEN_1)
        add(OnboardScreen.SCREEN_2)
        add(OnboardScreen.SCREEN_3)
        add(OnboardScreen.SCREEN_4)
        // Bạn có thể thêm điều kiện if (...) add(...) ở đây
    }

    override fun getItemCount(): Int = screens.size

    override fun createFragment(position: Int): Fragment {
        return when (screens[position]) {
            OnboardScreen.SCREEN_1 -> OnboardScreen1Fragment.newInstance()
            OnboardScreen.SCREEN_2 -> OnboardScreen2Fragment.newInstance()
            OnboardScreen.SCREEN_3 -> OnboardScreen3Fragment.newInstance()
            OnboardScreen.SCREEN_4 -> OnboardScreen4Fragment.newInstance()
        }
    }

    fun getScreenPosition(screen: OnboardScreen): Int {
        return screens.indexOf(screen)
    }
}
