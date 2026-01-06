package pion.datlt.libads.iap.model

import com.android.billingclient.api.BillingClient.ProductType

open class ProductModel(
    val productId: String,
    val productName: String,
    val productType: String,
    val productTitle: String,
    val productDescription: String,
    val isPurchase: Boolean,
    val purchaseTime: Long = 0L,
) {
    override fun toString(): String =
        "ProductModel(" +
            "productId='$productId', " +
            "productName='$productName', " +
            "productType='$productType', " +
            "productTitle='$productTitle', " +
            "isPurchase=$isPurchase, " +
            "purchaseTime=$purchaseTime" +
            ")"
}

class InAppProductModel(
    productId: String,
    productName: String,
    productTitle: String,
    productDescription: String,
    isPurchase: Boolean,
    purchaseTime: Long = 0L,
    val priceCurrencyCode: String,
    val formattedPrice: String,
    val offerTag: List<String>?,
) : ProductModel(
        productId = productId,
        productName = productName,
        productType = ProductType.INAPP,
        productTitle = productTitle,
        productDescription = productDescription,
        isPurchase = isPurchase,
        purchaseTime = purchaseTime,
    ) {
    override fun toString(): String =
        "InAppProductModel(" +
            "productId='$productId', " +
            "productName='$productName', " +
            "productType='$productType', " +
            "productTitle='$productTitle', " +
            "isPurchase=$isPurchase, " +
            "purchaseTime=$purchaseTime, " +
            "priceCurrencyCode='$priceCurrencyCode', " +
            "formattedPrice='$formattedPrice', " +
            "offerTag=$offerTag" +
            ")"
}

class SubscriptionProductModel(
    productId: String,
    productName: String,
    productTitle: String,
    productDescription: String,
    isPurchase: Boolean,
    purchaseTime: Long = 0L,
    val listBasePlan: List<BasePlanModel>,
) : ProductModel(
        productId = productId,
        productName = productName,
        productType = ProductType.SUBS,
        productTitle = productTitle,
        productDescription = productDescription,
        isPurchase = isPurchase,
        purchaseTime = purchaseTime,
    ) {
    override fun toString(): String =
        "SubscriptionProductModel(" +
            "productId='$productId', " +
            "productName='$productName', " +
            "productType='$productType', " +
            "productTitle='$productTitle', " +
            "isPurchase=$isPurchase, " +
            "purchaseTime=$purchaseTime, " +
            "listBasePlan=${listBasePlan.size} plans" +
            ")"
}
