package pion.tech.pionbase.base.firebaseAnalytics

import android.os.Bundle

interface FirebaseAnalyticsLogger {
    fun logEvent(eventName: String)

    fun logEvent(
        eventName: String,
        bundle: Bundle,
    )

    fun logEvent(
        eventName: String,
        block: Bundle.() -> Unit,
    )

    fun logEvent(
        eventName: String,
        paramName: String,
        paramValue: String,
    )

    fun logScreen(screenName: String)
}
