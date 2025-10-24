package pion.datlt.libads.utils

import android.util.Log
import android.widget.Toast
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import pion.datlt.libads.AdsController

fun AdsController.requestConsentInfoUpdate(
    onSuccess: (isRequire: Boolean, isConsentAvailable: Boolean) -> Unit,
    onFailed: (error: String) -> Unit
) {
    val debugSettings = ConsentDebugSettings.Builder(activity)
        .addTestDeviceHashedId("33BE2250B43518CCDA7DE426D04EE231")
        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        .build()

    val params = ConsentRequestParameters
        .Builder()
        .setTagForUnderAgeOfConsent(false)
        .apply {
            if (AdsConstant.isDebug) {
                setConsentDebugSettings(debugSettings)
            }
        }
        .build()


    consentInformation.requestConsentInfoUpdate(
        activity,
        params,
        {
            //OnConsentInfoUpdateSuccessListener
            val status = when (consentInformation.consentStatus) {
                ConsentInformation.ConsentStatus.NOT_REQUIRED -> {
                    "NOT_REQUIRED no need user consent"
                }

                ConsentInformation.ConsentStatus.OBTAINED -> {
                    "OBTAINED had consent from user"
                }

                ConsentInformation.ConsentStatus.REQUIRED -> {
                    "REQUIRED neeed user consent"
                }

                ConsentInformation.ConsentStatus.UNKNOWN -> {
                    "UNKNOWN status not determined"
                }

                else -> {
                    "error"
                }
            }
            val isRequire =
                consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED
            onSuccess.invoke(isRequire, consentInformation.isConsentFormAvailable)
        },
        { requestConsentError ->
            // Consent gathering failed.
            //OnConsentInfoUpdateFailureListener

            onFailed.invoke("get info failed -> code ${requestConsentError.errorCode} : ${requestConsentError.message}")
        })


}

fun AdsController.loadAndShowConsentFormIfRequire(
    onConsentDone: () -> Unit,
    onConsentError: (error: String) -> Unit
) {
    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
        activity
    ) { loadAndShowError ->
        loadAndShowError?.let {
            //loi
            onConsentError.invoke("consent error -> code ${loadAndShowError.errorCode} : ${loadAndShowError.message}")
        } ?: kotlin.run {
            //khong loi
            onConsentDone.invoke()
        }
    }
}

fun AdsController.loadAndShowConsentForm(
    onConsentDone: () -> Unit,
    onConsentError: (error: String) -> Unit
) {
    UserMessagingPlatform.loadConsentForm(activity, {consentForm ->
        //load consent thanh cong
        consentForm.show(activity){consentError ->
            consentError?.let {
                //loi
                onConsentError.invoke("consent error -> code ${consentError.errorCode} : ${consentError.message}")
            } ?: kotlin.run {
                //khong loi
                onConsentDone.invoke()
            }
        }
    }, {formError ->
        //load consent failed
        onConsentError.invoke("load consent form failed code ${formError.errorCode} : ${formError.message}")
    })
}

fun AdsController.showPolicyForm(
    onShow: () -> Unit,
    onError: (error: String) -> Unit
){
    UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
        formError?.let {
            onError.invoke("form error -> code ${it.errorCode} : ${it.message}")
        } ?: kotlin.run {
            onShow.invoke()
        }
    }
}


fun AdsController.resetConsent() {
    consentInformation.reset()
    Toast.makeText(activity, "reset consent", Toast.LENGTH_SHORT).show()
}

fun AdsController.isNeedToShowConsent() : Boolean{
    return when (consentInformation.consentStatus) {
        ConsentInformation.ConsentStatus.NOT_REQUIRED -> {
            "NOT_REQUIRED no need user consent"
            false
        }

        ConsentInformation.ConsentStatus.OBTAINED -> {
            "OBTAINED had consent from user"
            true
        }

        ConsentInformation.ConsentStatus.REQUIRED -> {
            "REQUIRED neeed user consent"
            true
        }

        ConsentInformation.ConsentStatus.UNKNOWN -> {
            "UNKNOWN status not determined"
            false
        }

        else -> {
            "error"
            false
        }
    }
}