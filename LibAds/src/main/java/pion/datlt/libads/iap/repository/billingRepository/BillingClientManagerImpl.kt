package pion.datlt.libads.iap.repository.billingRepository

import android.app.Application
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import pion.datlt.libads.iap.utils.BillingResponseCode

/**
 * Manages BillingClient lifecycle and connection state.
 * Provides suspend functions for connecting to Google Play Billing.
 */
class BillingClientManagerImpl(
    private val application: Application,
    private val purchasesUpdatedListener: PurchasesUpdatedListener,
) : BillingClientManager {

    init {
        ensureBillingClientCreated()
    }

    private var billingClient: BillingClient? = null

    /**
     * Gets the BillingClient if it's ready for use.
     * @return BillingClient if connected and ready, null otherwise
     */
    override fun getBillingClient(): BillingClient? {
        val client = billingClient ?: return null
        return if (client.isReady) client else null
    }

    /**
     * Starts connection to BillingClient without timeout.
     * @return true if connection successful, false otherwise
     */
    override suspend fun startConnection(): Boolean = connectInternal(timeoutMs = null)

    /**
     * Starts connection to BillingClient with timeout.
     * @param timeoutMs Maximum time to wait for connection in milliseconds
     * @return true if connection successful within timeout, false otherwise
     */
    override suspend fun startConnectionWithTimeout(timeoutMs: Long): Boolean = connectInternal(timeoutMs)

    /**
     * Internal connection logic with optional timeout.
     * @param timeoutMs Timeout in milliseconds, or null for no timeout
     * @return true if connection successful, false otherwise
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun connectInternal(timeoutMs: Long?): Boolean {
        // Already connected
        if (billingClient?.isReady == true) return true

        return try {
            val connectionBlock: suspend () -> Boolean = {
                suspendCancellableCoroutine { cont ->
                    ensureBillingClientCreated()

                    billingClient?.startConnection(
                        object : BillingClientStateListener {
                            override fun onBillingSetupFinished(billingResult: BillingResult) {
                                if (!cont.isActive) return
                                cont.resume(
                                    BillingResponseCode.isSuccess(billingResult.responseCode),
                                    null,
                                )
                            }

                            override fun onBillingServiceDisconnected() {
                                // Connection will be retried on next request
                            }
                        },
                    )
                }
            }

            if (timeoutMs != null) {
                withTimeout(timeoutMs) { connectionBlock() }
            } else {
                connectionBlock()
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun ensureBillingClientCreated() {
        if (billingClient == null) {
            val pendingPurchasesParams =
                PendingPurchasesParams
                    .newBuilder()
                    .enableOneTimeProducts()
                    .build()

            billingClient =
                BillingClient
                    .newBuilder(application)
                    .setListener(purchasesUpdatedListener)
                    .enablePendingPurchases(pendingPurchasesParams)
                    .build()
        }
    }

    override fun endConnection() {
        billingClient?.endConnection()
        billingClient = null
    }
}
