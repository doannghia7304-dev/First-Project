package pion.tech.pionbase.base.firebaseAnalytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class FirebaseAnalyticsLoggerImpl(
    private val logger: FirebaseAnalytics,
) : FirebaseAnalyticsLogger {
    companion object {
        private const val TAG = "TESTERCHECKTRACKING"
    }

    override fun logEvent(eventName: String) {
        Timber.tag(TAG).d("logEvent: $eventName")
        logger.logEvent(eventName, Bundle.EMPTY)
    }

    override fun logEvent(
        eventName: String,
        bundle: Bundle,
    ) {
        val stringBuilder = StringBuilder()
        val keys: Set<String> = bundle.keySet()
        for (key in keys) {
            val value: Any? = bundle.get(key)
            stringBuilder.append("($key : $value) ")
        }
        Timber.tag(TAG).d("logEvent: $eventName $stringBuilder")
        logger.logEvent(eventName, bundle)
    }

    override fun logEvent(
        eventName: String,
        block: Bundle.() -> Unit,
    ) {
        val bundle = Bundle().apply(block)
        val stringBuilder = StringBuilder()
        val keys: Set<String> = bundle.keySet()
        for (key in keys) {
            val value: Any? = bundle.get(key)
            stringBuilder.append("($key : $value) ")
        }
        Timber.tag(TAG).d("logEvent: $eventName $stringBuilder")
        logger.logEvent(eventName, bundle)
    }

    override fun logEvent(
        eventName: String,
        paramName: String,
        paramValue: String,
    ) {
        val bundle = Bundle()
        bundle.putString(paramName, paramValue)
        logger.logEvent(eventName, bundle)
        Timber.tag(TAG).d("logEvent: $eventName ($paramName : $paramValue)")
    }

    override fun logScreen(screenName: String) {
        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
        logger.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        Timber.tag(TAG).d("logScreen: $screenName")
    }
}
