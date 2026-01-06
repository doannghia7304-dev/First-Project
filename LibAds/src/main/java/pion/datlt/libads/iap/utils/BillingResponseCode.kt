package pion.datlt.libads.iap.utils

import com.android.billingclient.api.BillingClient

/**
 * Constants for BillingClient response codes and their human-readable messages.
 */
object BillingResponseCode {
    fun getErrorMessage(code: Int): String =
        when (code) {
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> "Service_Timeout"
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "Feature_Not_Supported"
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "Service_Disconnected"
            BillingClient.BillingResponseCode.USER_CANCELED -> "User_Canceled"
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "Service_Unavailable"
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Billing_Unavailable"
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "Item_Unavailable"
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "Developer_Error"
            BillingClient.BillingResponseCode.ERROR -> "Error"
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "Item_Already_Owned"
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "Item_Not_Owned"
            else -> "Unknown_Error"
        }

    fun isSuccess(code: Int): Boolean = code == BillingClient.BillingResponseCode.OK
}