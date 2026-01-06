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
    companion object {
        private const val DEFAULT_TIMEOUT_MS = 7000L
    }

    init {
        ensureBillingClientCreated()
    }

    private var billingClient: BillingClient? = null

    override val client: BillingClient?
        get() = billingClient

    override val isReady: Boolean
        get() = billingClient?.isReady == true

    /**
     * Starts connection to BillingClient without timeout.
     * @return true if connection successful, false otherwise
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun startConnection(): Boolean {
        return try {
            if (isReady) return true

            suspendCancellableCoroutine { cont ->
                ensureBillingClientCreated()

                billingClient?.startConnection(
                    object : BillingClientStateListener {
                        override fun onBillingSetupFinished(billingResult: BillingResult) {
                            if (!cont.isActive) return
                            cont.resume(
                                BillingResponseCode.isSuccess(billingResult.responseCode),
                                null
                            )
                        }

                        override fun onBillingServiceDisconnected() {
                            // Connection will be retried on next request
                        }
                    },
                )
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Starts connection to BillingClient with timeout.
     * @param timeoutMs Maximum time to wait for connection in milliseconds
     * @return true if connection successful within timeout, false otherwise
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun startConnectionWithTimeout(timeoutMs: Long): Boolean {
        return try {
            if (isReady) return true

            withTimeout(timeoutMs) {
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