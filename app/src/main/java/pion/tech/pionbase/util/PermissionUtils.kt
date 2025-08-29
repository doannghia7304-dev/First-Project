package pion.tech.pionbase.util

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.piontech.core.base.doActionWhenStop
import pion.datlt.libads.AdsController
import pion.tech.pionbase.R

fun Fragment.requestPermissionInSetting(
    launcher: ActivityResultLauncher<Intent>,
    intent: Intent,
) {
    doActionWhenStop {
        AdsController.isBlockOpenAds = true
    }
    runCatching {
        launcher.launch(intent)
    }.onFailure {
        displayToast(R.string.something_error)
    }
}
