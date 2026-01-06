package pion.datlt.libads.iap.repository.purchaseRepository

import android.app.Activity
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

/**
 * Interface for PurchaseRepository to enable dependency injection and testing.
 */
interface PurchaseRepository {
    suspend fun queryAllPurchases(): List<Purchase>
    suspend fun queryPurchases(productType: String): List<Purchase>?
    fun acknowledgePurchase(purchase: Purchase, onSuccess: () -> Unit)
    fun launchInAppPurchaseFlow(activity: Activity, productDetails: ProductDetails)
    fun launchSubscriptionPurchaseFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String,
        oldPurchaseToken: String? = null
    )
    /**
     * Consumes all in-app purchases (for debug/testing only).
     * @param onComplete Callback when all purchases are consumed
     */
    fun consumeAllInAppPurchases(onComplete: () -> Unit)
}
