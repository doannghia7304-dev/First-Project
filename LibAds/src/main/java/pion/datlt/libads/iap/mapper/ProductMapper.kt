package pion.datlt.libads.iap.mapper

import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import pion.datlt.libads.iap.model.BasePlanModel
import pion.datlt.libads.iap.model.IapIdModel
import pion.datlt.libads.iap.model.InAppProductModel
import pion.datlt.libads.iap.model.PricingPhaseModel
import pion.datlt.libads.iap.model.ProductModel
import pion.datlt.libads.iap.model.SubscriptionProductModel

/**
 * Maps Google Play Billing ProductDetails to app-specific ProductModel.
 */
object ProductMapper {
    /**
     * Converts a list of ProductDetails to ProductModel list.
     * @param productDetailsList List of ProductDetails from Google Play
     * @param iapIdModels Configuration list to filter base plans
     * @return List of ProductModel (InAppProductModel or SubscriptionProductModel)
     */
    fun mapToProductModels(
        productDetailsList: List<ProductDetails>,
        iapIdModels: List<IapIdModel>,
    ): List<ProductModel> =
        productDetailsList.mapNotNull { productDetails ->
            when (productDetails.productType) {
                ProductType.INAPP -> mapInAppProduct(productDetails)
                ProductType.SUBS -> mapSubscriptionProduct(productDetails, iapIdModels)
                else -> null
            }
        }

    /**
     * Merges product models with purchase information.
     * @param products List of ProductModel
     * @param purchases List of Purchase from Google Play
     * @return List of ProductModel with updated purchase status
     */
    fun mergeWithPurchases(
        products: List<ProductModel>,
        purchases: List<Purchase>,
    ): List<ProductModel> {
        val acknowledgedPurchases =
            purchases.filter {
                it.purchaseState == Purchase.PurchaseState.PURCHASED && it.isAcknowledged
            }

        return products.map { product ->
            val purchase = acknowledgedPurchases.find { product.productId in it.products }
            copyWithPurchaseInfo(product, purchase)
        }
    }

    private fun mapInAppProduct(productDetails: ProductDetails): InAppProductModel? {
        val offerDetails = productDetails.oneTimePurchaseOfferDetails ?: return null

        return InAppProductModel(
            productId = productDetails.productId,
            productName = productDetails.name,
            productTitle = productDetails.title,
            productDescription = productDetails.description,
            priceCurrencyCode = offerDetails.priceCurrencyCode,
            formattedPrice = offerDetails.formattedPrice,
            isPurchase = false,
            purchaseTime = 0L,
            offerTag = offerDetails.offerTags,
        )
    }

    private fun mapSubscriptionProduct(
        productDetails: ProductDetails,
        iapIdModels: List<IapIdModel>,
    ): SubscriptionProductModel? {
        val subscriptionOffers = productDetails.subscriptionOfferDetails
        if (subscriptionOffers.isNullOrEmpty()) return null

        val productId = productDetails.productId
        val configuredBasePlans =
            iapIdModels
                .find { it.idProduct == productId }
                ?.listBasePlan
                ?: emptyList()

        val basePlanMap = HashMap<String, ArrayList<BasePlanModel>>()

        for (offer in subscriptionOffers) {
            val basePlanId = offer.basePlanId

            // Only include base plans that are configured
            val isConfigured = configuredBasePlans.any { it.basePlanId == basePlanId }
            if (!isConfigured) continue

            val pricingPhases =
                offer.pricingPhases.pricingPhaseList.map { phase ->
                    PricingPhaseModel(
                        priceCurrencyCode = phase.priceCurrencyCode,
                        recurrenceMode = phase.recurrenceMode,
                        priceAmountMicros = phase.priceAmountMicros,
                        formattedPrice = phase.formattedPrice,
                        billingPeriod = phase.billingPeriod,
                        billingCycleCount = phase.billingCycleCount,
                    )
                }

            val basePlan =
                BasePlanModel(
                    productId = productId,
                    basePlanId = basePlanId,
                    offerId = offer.offerId,
                    offerTag = offer.offerTags,
                    offerToken = offer.offerToken,
                    listPricingPhase = pricingPhases,
                )

            basePlanMap.getOrPut(basePlanId) { ArrayList() }.add(basePlan)
        }

        // Select best base plan for each basePlanId
        val selectedBasePlans =
            basePlanMap.values.mapNotNull { plans ->
                selectBestBasePlan(plans, productId, configuredBasePlans)
            }

        return SubscriptionProductModel(
            productId = productId,
            productName = productDetails.name,
            productTitle = productDetails.title,
            productDescription = productDetails.description,
            listBasePlan = selectedBasePlans,
            isPurchase = false,
            purchaseTime = 0L,
        )
    }

    private fun selectBestBasePlan(
        plans: List<BasePlanModel>,
        productId: String,
        configuredBasePlans: List<pion.datlt.libads.iap.model.BasePlanIdModel>,
    ): BasePlanModel? {
        if (plans.isEmpty()) return null
        if (plans.size == 1) return plans.first()

        // Priority 1: Match configured offerId
        val configuredOffer =
            plans.firstOrNull { plan ->
                val config = configuredBasePlans.find { it.basePlanId == plan.basePlanId }
                plan.offerId == config?.offerId
            }
        if (configuredOffer != null) return configuredOffer

        // Priority 2: Base plan without offer (offerId = null)
        val baseOffer = plans.firstOrNull { it.offerId == null }
        if (baseOffer != null) return baseOffer

        // Fallback: First available
        return plans.first()
    }

    private fun copyWithPurchaseInfo(
        product: ProductModel,
        purchase: Purchase?,
    ): ProductModel {
        val isPurchased = purchase != null
        val purchaseTime = purchase?.purchaseTime ?: 0L

        return when (product) {
            is InAppProductModel -> {
                InAppProductModel(
                    productId = product.productId,
                    productName = product.productName,
                    productTitle = product.productTitle,
                    productDescription = product.productDescription,
                    isPurchase = isPurchased,
                    purchaseTime = purchaseTime,
                    priceCurrencyCode = product.priceCurrencyCode,
                    formattedPrice = product.formattedPrice,
                    offerTag = product.offerTag,
                )
            }

            is SubscriptionProductModel -> {
                SubscriptionProductModel(
                    productId = product.productId,
                    productName = product.productName,
                    productTitle = product.productTitle,
                    productDescription = product.productDescription,
                    isPurchase = isPurchased,
                    purchaseTime = purchaseTime,
                    listBasePlan = product.listBasePlan,
                )
            }

            else -> {
                product
            }
        }
    }
}
