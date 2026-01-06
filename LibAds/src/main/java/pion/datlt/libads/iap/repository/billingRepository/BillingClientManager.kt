package pion.datlt.libads.iap.repository.billingRepository

import com.android.billingclient.api.BillingClient

/**
 * Interface for BillingClientManager to enable dependency injection and testing.
 */
interface BillingClientManager {
    /**
     * Gets the BillingClient if it's ready for use.
     * @return BillingClient if connected and ready, null otherwise
     */
    fun getBillingClient(): BillingClient?

    /**
     * Starts connection to BillingClient without timeout.
     * @return true if connection successful, false otherwise
     */
    suspend fun startConnection(): Boolean

    /**
     * Starts connection to BillingClient with timeout.
     * @param timeoutMs Maximum time to wait for connection in milliseconds
     * @return true if connection successful within timeout, false otherwise
     */
    suspend fun startConnectionWithTimeout(timeoutMs: Long = 7000L): Boolean

    /**
     * Ends connection and releases BillingClient.
     */
    fun endConnection()
}