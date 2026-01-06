package pion.datlt.libads.iap.repository.purchaseRepository

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import pion.datlt.libads.iap.utils.BillingResponseCode
import pion.datlt.libads.iap.repository.billingRepository.BillingClientManager

/**
 * Repository for handling purchases, acknowledgements, and billing flows.
 * Uses BillingClientManager to always get the current BillingClient instance.
 */
class PurchaseRepositoryImpl(
    private val billingClientManager: BillingClientManager,
) : PurchaseRepository {

    /**
     * Queries all active purchases (both INAPP and SUBS).
     * @return List of active Purchase objects, empty if not connected
     */
    override suspend fun queryAllPurchases(): List<Purchase> {
        val subsResult = queryPurchases(ProductType.SUBS)
        val inAppResult = queryPurchases(ProductType.INAPP)

        if (subsResult == null || inAppResult == null) return emptyList()
        return subsResult + inAppResult
    }

    /**
     * Queries purchases for a specific product type.
     * @param productType INAPP or SUBS
     * @return List of Purchase or null if error/not connected
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun queryPurchases(productType: String): List<Purchase>? {
        val client = billingClientManager.getBillingClient() ?: return null

        return suspendCancellableCoroutine { cont ->
            client.queryPurchasesAsync(
                QueryPurchasesParams
                    .newBuilder()
                    .setProductType(productType)
                    .build(),
            ) { billingResult, purchases ->
                if (BillingResponseCode.isSuccess(billingResult.responseCode)) {
                    cont.resume(purchases, null)
                } else {
                    cont.resume(null, null)
                }
            }
        }
    }

    /**
     * Acknowledges a purchase.
     * @param purchase The purchase to acknowledge
     * @param onSuccess Callback when acknowledgement succeeds
     */
    override fun acknowledgePurchase(
        purchase: Purchase,
        onSuccess: () -> Unit,
    ) {
        val client = billingClientManager.getBillingClient() ?: return

        val params =
            AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        client.acknowledgePurchase(params) { billingResult ->
            if (BillingResponseCode.isSuccess(billingResult.responseCode)) {
                onSuccess()
            }
        }
    }

    /**
     * Launches billing flow for in-app purchase.
     */
    override fun launchInAppPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
    ) {
        val client = billingClientManager.getBillingClient() ?: return

        val params =
            BillingFlowParams.ProductDetailsParams
                .newBuilder()
                .setProductDetails(productDetails)
                .build()

        val billingFlowParams =
            BillingFlowParams
                .newBuilder()
                .setProductDetailsParamsList(listOf(params))
                .build()

        client.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Launches billing flow for subscription purchase.
     * @param activity Activity context
     * @param productDetails Product details
     * @param offerToken Offer token for the selected base plan
     * @param oldPurchaseToken Token of existing subscription for upgrade/downgrade (optional)
     */
    override fun launchSubscriptionPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String,
        oldPurchaseToken: String?,
    ) {
        val client = billingClientManager.getBillingClient() ?: return

        val productDetailsParams =
            BillingFlowParams.ProductDetailsParams
                .newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()

        val billingFlowParamsBuilder =
            BillingFlowParams
                .newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))

        // Add subscription update params if upgrading/downgrading
        if (oldPurchaseToken != null) {
            val subscriptionUpdateParams =
                BillingFlowParams.SubscriptionUpdateParams
                    .newBuilder()
                    .setOldPurchaseToken(oldPurchaseToken)
                    .setSubscriptionReplacementMode(
                        BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE,
                    ).build()
            billingFlowParamsBuilder.setSubscriptionUpdateParams(subscriptionUpdateParams)
        }

        client.launchBillingFlow(activity, billingFlowParamsBuilder.build())
    }

    /**
     * Consumes all in-app purchases (for debug/testing only).
     * @param onComplete Callback when all purchases are consumed
     */
    override fun consumeAllInAppPurchases(onComplete: () -> Unit) {
        val client = billingClientManager.getBillingClient() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val purchasesResult = client.queryPurchasesAsync(
                QueryPurchasesParams
                    .newBuilder()
                    .setProductType(ProductType.INAPP)
                    .build(),
            )
            if (BillingResponseCode.isSuccess(purchasesResult.billingResult.responseCode)) {
                purchasesResult.purchasesList.forEach { purchase ->
                    val consumeParams =
                        ConsumeParams
                            .newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                    client.consumePurchase(consumeParams)
                }
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }
}
