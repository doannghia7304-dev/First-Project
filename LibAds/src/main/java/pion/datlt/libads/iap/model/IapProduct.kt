package pion.datlt.libads.iap.model

import com.android.billingclient.api.BillingClient.ProductType

/**
 * Base sealed interface for all product types.
 * Using sealed interface allows exhaustive when expressions and type-safe copying.
 */
sealed interface ProductModel {
    val productId: String
    val productName: String
    val productType: String
    val productTitle: String
    val productDescription: String
    val isPurchase: Boolean
    val purchaseTime: Long

    /** Creates a copy with updated purchase status */
    fun copyWithPurchaseStatus(
        isPurchase: Boolean,
        purchaseTime: Long,
    ): ProductModel
}

/**
 * Model for in-app (one-time) purchases.
 */
data class InAppProductModel(
    override val productId: String,
    override val productName: String,
    override val productTitle: String,
    override val productDescription: String,
    override val isPurchase: Boolean,
    override val purchaseTime: Long = 0L,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    val offerTag: List<String>?,
) : ProductModel {
    override val productType: String = ProductType.INAPP

    override fun copyWithPurchaseStatus(
        isPurchase: Boolean,
        purchaseTime: Long,
    ): InAppProductModel = copy(isPurchase = isPurchase, purchaseTime = purchaseTime)
}

/**
 * Model for subscription purchases.
 */
data class SubscriptionProductModel(
    override val productId: String,
    override val productName: String,
    override val productTitle: String,
    override val productDescription: String,
    override val isPurchase: Boolean,
    override val purchaseTime: Long = 0L,
    val listBasePlan: List<BasePlanModel>,
) : ProductModel {
    override val productType: String = ProductType.SUBS

    override fun copyWithPurchaseStatus(
        isPurchase: Boolean,
        purchaseTime: Long,
    ): SubscriptionProductModel = copy(isPurchase = isPurchase, purchaseTime = purchaseTime)
}
