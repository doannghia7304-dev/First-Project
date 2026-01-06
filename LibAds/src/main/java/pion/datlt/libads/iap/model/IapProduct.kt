package com.example.libiap.model

import com.android.billingclient.api.BillingClient.ProductType

open class ProductModel(
    val productId: String,
    val productName: String,
    val productType: String,
    val productTitle: String,
    val productDescription: String,
    val isPurchase: Boolean,
    val purchaseTime: Long = 0L,
)


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
    purchaseTime = purchaseTime
)


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
    purchaseTime = purchaseTime
)