package pion.tech.pionbase.feature.onboard.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen1Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen2Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen3Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen4Fragment
import pion.tech.pionbase.feature.onboard.viewpager.OnboardScreen5Fragment

class OnboardFragmentStateAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    private val listScreen = mutableListOf<Fragment>()

    init {
        // Add fragments in the order they should appear
        listScreen.add(OnboardScreen1Fragment())
        listScreen.add(OnboardScreen2Fragment())
        listScreen.add(OnboardScreen3Fragment())
        listScreen.add(OnboardScreen4Fragment())
    }

    override fun getItemCount(): Int = listScreen.size

    override fun createFragment(position: Int): Fragment = listScreen[position]
}
