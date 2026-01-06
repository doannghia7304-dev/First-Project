package pion.datlt.libads.iap.repository.billingRepository

import com.android.billingclient.api.BillingClient

/**
 * Interface for BillingClientManager to enable dependency injection and testing.
 */
interface BillingClientManager {
    val client: BillingClient?
    val isReady: Boolean
    suspend fun startConnection(): Boolean
    suspend fun startConnectionWithTimeout(timeoutMs: Long= 7000L): Boolean
    fun endConnection()
}