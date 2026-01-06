package pion.datlt.libads.iap

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pion.datlt.libads.iap.utils.BillingResponseCode
import pion.datlt.libads.iap.mapper.ProductMapper
import pion.datlt.libads.iap.model.BasePlanModel
import pion.datlt.libads.iap.model.IapIdModel
import pion.datlt.libads.iap.model.InAppProductModel
import pion.datlt.libads.iap.model.ProductModel
import pion.datlt.libads.iap.model.SubscriptionProductModel
import pion.datlt.libads.iap.utils.Utils
import pion.datlt.libads.iap.di.IapControllerFactory
import pion.datlt.libads.iap.di.IapProvider

/**
 * Facade for Google Play Billing operations.
 * Manages IAP initialization, product queries, and purchase flows.
 */
object IapController {
    private var isDebug = false

    private val listID = mutableListOf<IapIdModel>()

    private var subscribeInterface: SubscribeInterface? = null

    private var currentBasePlanId: String? = null

    fun getCurrentBasePlanId(): String? = currentBasePlanId

    fun setIAPListener(listener: SubscribeInterface) {
        subscribeInterface = listener
    }

    private val listProductModel = mutableListOf<ProductModel>()

    fun getListAllProduct(): List<ProductModel> = listProductModel

    /**
     * Initializes IAP with configuration from JSON file.
     * Connects to BillingClient, queries products and purchases, and merges them.
     * @return true if initialization successful, false otherwise
     */
    suspend fun initIap(
        application: Application,
        pathJson: String,
        isDebug: Boolean,
    ): Boolean {
        this.isDebug = isDebug
        listID.clear()
        listID.addAll(Utils.getDataInput(application, pathJson))
        Log.d("asgawgawgawg", "chay vao day1")

        // Initialize dependencies using factory
        val dependencies = IapControllerFactory.create(application, ::onPurchaseUpdated)
        Log.d("asgawgawgawg", "chay vao day2")
        IapProvider.initialize(dependencies)
        Log.d("asgawgawgawg", "chay vao day3")

        // Connect to BillingClient
        val isConnectSuccess = dependencies.billingClientManager.startConnectionWithTimeout()
        Log.d("asgawgawgawg", "chay vao day4")
        if (!isConnectSuccess) {
            Log.d("asgawgawgawg", "chay vao day")
            return false
        }

        // Query products and purchases
        val allProductDetails = dependencies.productRepository.queryAllProducts(listID)
        val allPurchases = dependencies.purchaseRepository.queryAllPurchases()

        // Map to product models and merge with purchase info
        val products = ProductMapper.mapToProductModels(allProductDetails, listID)
        val productsWithPurchases = ProductMapper.mergeWithPurchases(products, allPurchases)
        Log.d("asgawgawgawg", "initIap: $products")

        // Update cached list
        listProductModel.clear()
        listProductModel.addAll(productsWithPurchases)

        return true
    }

    /**
     * Handles purchase updates from Google Play Billing.
     * Acknowledges purchases and notifies listener.
     */
    private fun onPurchaseUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?,
    ) {
        if (BillingResponseCode.isSuccess(billingResult.responseCode) && purchases != null) {
            for (purchase in purchases) {
                if (!purchase.isAcknowledged) {
                    // Acknowledge purchase
                    IapProvider.getDependencies()?.purchaseRepository?.acknowledgePurchase(purchase) {
                        // Update product model and notify success
                        updateProductPurchaseStatus(purchase, true)
                    }
                } else {
                    // Already acknowledged, just update status and notify
                    updateProductPurchaseStatus(purchase, true)
                }
            }
        } else {
            subscribeInterface?.subscribeError(
                BillingResponseCode.getErrorMessage(billingResult.responseCode),
            )
        }
    }

    private fun updateProductPurchaseStatus(
        purchase: Purchase,
        isPurchased: Boolean,
    ) {
        purchase.products.forEach { productId ->
            listProductModel.find { it.productId == productId }?.let { product ->
                val updatedProduct =
                    copyWithPurchaseStatus(product, isPurchased, purchase.purchaseTime)
                val index = listProductModel.indexOf(product)
                if (index >= 0) {
                    listProductModel[index] = updatedProduct
                }
                subscribeInterface?.subscribeSuccess(updatedProduct)
            }
        }
    }

    private fun copyWithPurchaseStatus(
        product: ProductModel,
        isPurchased: Boolean,
        purchaseTime: Long,
    ): ProductModel =
        when (product) {
            is InAppProductModel -> {
                InAppProductModel(
                    productId = product.productId,
                    productName = product.productName,
                    productTitle = product.productTitle,
                    productDescription = product.productDescription,
                    isPurchase = isPurchased,
                    purchaseTime = if (isPurchased) purchaseTime else 0L,
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
                    purchaseTime = if (isPurchased) purchaseTime else 0L,
                    listBasePlan = product.listBasePlan,
                )
            }

            else -> {
                product
            }
        }

    fun getProduct(productID: String): ProductModel? = listProductModel.find { it.productId == productID }

    fun getBasePlan(
        productID: String,
        basePlanId: String,
    ): BasePlanModel? =
        try {
            (listProductModel.find { it.productId == productID } as? SubscriptionProductModel)
                ?.listBasePlan
                ?.find { it.basePlanId == basePlanId }
        } catch (e: Exception) {
            null
        }

    fun hasAnyPurchasedProduct(listProductId: List<String>): Boolean =
        try {
            listProductModel.filter { it.productId in listProductId }.any { it.isPurchase }
        } catch (e: Exception) {
            false
        }

    /**
     * Resets all in-app purchases (debug mode only).
     * Consumes all purchased in-app products.
     */
    fun resetIap(activity: Activity) {
        if (isDebug) {
            IapProvider.getDependencies()?.purchaseRepository?.consumeAllInAppPurchases(activity) {
                Toast
                    .makeText(
                        activity,
                        "Reset IAP InApps Finish",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }

    /**
     * Launches billing flow for product purchase.
     * @param activity Activity context
     * @param productId Product ID to purchase
     * @param basePlanId Base plan ID for subscriptions (required for SUBS, optional for INAPP)
     */
    fun buyIap(
        activity: Activity,
        productId: String,
        basePlanId: String? = null,
    ) {
        val product = getProduct(productId) ?: return
        val dependencies = IapProvider.getDependencies() ?: return

        CoroutineScope(Dispatchers.IO).launch {
            // Reconnect if needed
            dependencies.billingClientManager.startConnection()

            when (product) {
                is InAppProductModel -> {
                    // Find ProductDetails for in-app purchase
                    val productDetails = findProductDetails(productId)
                    if (productDetails != null) {
                        withContext(Dispatchers.Main) {
                            dependencies.purchaseRepository.launchInAppPurchaseFlow(activity, productDetails)
                        }
                    }
                }

                is SubscriptionProductModel -> {
                    // Find ProductDetails and offer token for subscription
                    val productDetails = findProductDetails(productId)
                    val offerToken =
                        productDetails
                            ?.subscriptionOfferDetails
                            ?.find { it.basePlanId == basePlanId }
                            ?.offerToken

                    if (productDetails != null && offerToken != null && basePlanId != null) {
                        // Check for existing subscription (upgrade/downgrade)
                        val oldPurchaseToken =
                            dependencies.purchaseRepository
                                .queryPurchases(ProductType.SUBS)
                                ?.find { purchase -> productId in purchase.products }
                                ?.purchaseToken

                        withContext(Dispatchers.Main) {
                            currentBasePlanId = basePlanId
                            Log.d("CHECKIAPPRODUCT", "buyIap: $currentBasePlanId")
                            dependencies.purchaseRepository.launchSubscriptionPurchaseFlow(
                                activity,
                                productDetails,
                                offerToken,
                                oldPurchaseToken,
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun findProductDetails(productId: String): ProductDetails? {
        val dependencies = IapProvider.getDependencies() ?: return null
        dependencies.billingClientManager.startConnection()
        val allDetails = dependencies.productRepository.queryAllProducts(listID)
        return allDetails.find { it.productId == productId }
    }

    /**
     * Releases all IAP resources and clears dependencies.
     * Should be called when IAP is no longer needed.
     */
    fun release() {
        listProductModel.clear()
        listID.clear()
        subscribeInterface = null
        currentBasePlanId = null
        IapProvider.getDependencies()?.billingClientManager?.endConnection()
        IapProvider.clear()
    }
}
