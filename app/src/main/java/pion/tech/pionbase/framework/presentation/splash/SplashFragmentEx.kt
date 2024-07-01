package pion.tech.pionbase.framework.presentation.splash

import android.os.Bundle
import androidx.activity.addCallback
import pion.tech.pionbase.R
import pion.tech.pionbase.framework.database.entities.DummyEntity
import pion.tech.pionbase.util.BundleKey

fun SplashFragment.backEvent() {
    activity?.onBackPressedDispatcher?.addCallback(this, true) {
        onBackPressed()
    }
//    binding.btnBack.setPreventDoubleClickScaleView {
//        onBackPressed()
//    }
}

fun SplashFragment.onBackPressed() {
//    findNavController().popBackStack()
}

fun SplashFragment.startAnimation() {
    binding.loadingView.startAnim(2000L) {
        val dummyEntity = DummyEntity(id = 0, value = "Hello Home")

        val bundle = Bundle()
        bundle.putParcelable(BundleKey.KEY_DUMMY_ENTITY, dummyEntity)

        safeNav(R.id.splashFragment, R.id.homeFragment, bundle)
    }
}