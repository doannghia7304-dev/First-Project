package pion.tech.pionbase.util

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import pion.tech.pionbase.R
import pion.tech.pionbase.base.doActionWhenStop

fun Fragment.requestPermissionInSetting(
    launcher: ActivityResultLauncher<Intent>,
    intent: Intent,
) {
    doActionWhenStop {
        //TODO : AdsController
//        AdsController.isBlockOpenAds = true
    }
    runCatching {
        launcher.launch(intent)
    }.onFailure {
        displayToast(R.string.something_error)
    }
}
