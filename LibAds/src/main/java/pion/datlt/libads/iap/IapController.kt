package com.example.libiap

import android.app.Activity
import android.app.Application
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import com.example.libiap.model.BasePlanModel
import com.example.libiap.model.IapIdModel
import com.example.libiap.model.ProductModel
import com.example.libiap.model.InAppProductModel
import com.example.libiap.model.PricingPhaseModel
import com.example.libiap.model.SubscriptionProductModel
import com.example.libiap.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.set


object IapController {

    private var isDebug = false

    private var billingClient: BillingClient? = null

    private val listID = mutableListOf<IapIdModel>()

    private var subscribeInterface: SubscribeInterface? = null

    private var currentBasePlanId : String? = null

    fun getCurrentBasePlanId() : String?{
        return currentBasePlanId
    }

    fun setIAPListener(listener: SubscribeInterface) {
        subscribeInterface = listener
    }

    private val listProductDetail = mutableListOf<ProductDetails>()

    private val listProductModel = mutableListOf<ProductModel>()

    fun getListAllProduct(): List<ProductModel> {
        return listProductModel
    }

    suspend fun initIap(
        application: Application,
        pathJson: String,
        isDebug: Boolean
    ): Boolean {
        this.isDebug = isDebug
        listID.clear()
        listID.addAll(Utils.getDataInput(application, pathJson))
        val isConnectSuccess = startConnection(application)
        if (isConnectSuccess) {
            //lay list all product
            val listAllProduct = getAllProductModel(application)
            val listAllPurchase = getAllPurchase(application)

            //lay list all purchase
            //map 2 list voi nhau
            listProductModel.clear()
            listAllProduct.forEach { iapProductModel ->
                val purchase = try {
                    listAllPurchase.filter { purchase ->
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged
                    }.find { purchase -> iapProductModel.productId in purchase.products }
                } catch (e: Exception) {
                    null
                }

                val item: ProductModel? = when (iapProductModel) {
                    is InAppProductModel -> {
                        InAppProductModel(
                            productId = iapProductModel.productId,
                            productName = iapProductModel.productName,
                            productTitle = iapProductModel.productTitle,
                            productDescription = iapProductModel.productDescription,
                            isPurchase = purchase != null,
                            purchaseTime = purchase?.purchaseTime ?: 0L,
                            priceCurrencyCode = iapProductModel.priceCurrencyCode,
                            formattedPrice = iapProductModel.formattedPrice,
                            offerTag = iapProductModel.offerTag
                        )
                    }

                    is SubscriptionProductModel -> {
                        //sub
                        SubscriptionProductModel(
                            productId = iapProductModel.productId,
                            productName = iapProductModel.productName,
                            productTitle = iapProductModel.productTitle,
                            productDescription = iapProductModel.productDescription,
                            isPurchase = purchase != null,
                            purchaseTime = purchase?.purchaseTime ?: 0L,
                            listBasePlan = iapProductModel.listBasePlan
                        )
                    }

                    else -> {
                        null
                    }
                }
                if (item != null) {
                    listProductModel.add(item)
                }
            }
            return true
        } else {
            return false
        }
    }


    /**
     * Thiết lập kết nối với Google Play Billing và đảm bảo [billingClient] sẵn sàng sử dụng.
     *
     * @param application Context ứng dụng, dùng để tạo BillingClient.
     * @param timeOut Thời gian tối đa (ms) để chờ kết nối thành công. Mặc định là 7000ms (7 giây).
     *
     * @return
     * - `true`  : BillingClient đã kết nối thành công và sẵn sàng.
     * - `false` : Có lỗi xảy ra hoặc kết nối thất bại (bao gồm timeout hoặc exception).
     *
     * @note
     * - Hàm là `suspend` và sử dụng coroutine với `suspendCancellableCoroutine` + `withTimeout`.
     * - Nếu [billingClient] đã sẵn sàng (`isReady == true`), hàm trả về ngay `true`.
     * - BillingClient sẽ được tạo nếu chưa tồn tại, và gọi `enablePendingPurchases` với các pending purchase.
     * - `onBillingSetupFinished` sẽ resume coroutine khi kết nối thành công hoặc thất bại.
     * - `onBillingServiceDisconnected` sẽ được gọi nếu dịch vụ bị ngắt, bạn có thể thử kết nối lại ở lần request sau.
     * - Sử dụng @OptIn(ExperimentalCoroutinesApi::class) vì `suspendCancellableCoroutine` trong withTimeout là experimental.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun startConnectionWithTimeout(
        application: Application,
        timeOut: Long = 7000L
    ): Boolean {
        return try {
            if (billingClient != null && billingClient?.isReady == true) {
                return true
            }
            withTimeout(timeOut) {
                suspendCancellableCoroutine { cont ->

                    if (billingClient == null) {
                        val pendingPurchasesParams = PendingPurchasesParams
                            .newBuilder()
                            .enableOneTimeProducts()
                            .build()

                        billingClient = BillingClient.newBuilder(application)
                            .setListener(::onPurchaseUpdated)
                            .enablePendingPurchases(pendingPurchasesParams)
                            .build()
                    }


                    billingClient?.startConnection(object : BillingClientStateListener {
                        override fun onBillingSetupFinished(billingResult: BillingResult) {
                            if (!cont.isActive) return
                            cont.resume(
                                billingResult.responseCode == BillingClient.BillingResponseCode.OK,
                                null
                            )
                        }

                        override fun onBillingServiceDisconnected() {
                            // Try to restart the connection on the next request to
                            // Google Play by calling the startConnection() method.
                        }
                    })
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun startConnection(application: Application): Boolean {
        return try {
            if (billingClient != null && billingClient?.isReady == true) {
                return true
            }
            suspendCancellableCoroutine { cont ->
                if (billingClient == null) {
                    val pendingPurchasesParams = PendingPurchasesParams
                        .newBuilder()
                        .enableOneTimeProducts()
                        .build()

                    billingClient = BillingClient.newBuilder(application)
                        .setListener(::onPurchaseUpdated)
                        .enablePendingPurchases(pendingPurchasesParams)
                        .build()
                }


                billingClient?.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (!cont.isActive) return
                        cont.resume(
                            billingResult.responseCode == BillingClient.BillingResponseCode.OK,
                            null
                        )
                    }

                    override fun onBillingServiceDisconnected() {
                        // Try to restart the connection on the next request to
                        // Google Play by calling the startConnection() method.
                    }
                })
            }
        } catch (e: Exception) {
            false
        }
    }


    private suspend fun getAllProductModel(
        application: Application
    ): List<ProductModel> = coroutineScope {
        val isConnectSuccess = startConnectionWithTimeout(application)
        if (!isConnectSuccess) return@coroutineScope emptyList()
        val listInAppProductQuery = mutableListOf<QueryProductDetailsParams.Product>()
        val listSubsProductQuery = mutableListOf<QueryProductDetailsParams.Product>()
        for (iapIdModel in listID) {
            when (iapIdModel.type) {
                ProductType.INAPP -> listInAppProductQuery.add(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(iapIdModel.idProduct)
                        .setProductType(ProductType.INAPP)
                        .build()
                )

                ProductType.SUBS -> listSubsProductQuery.add(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(iapIdModel.idProduct)
                        .setProductType(ProductType.SUBS)
                        .build()
                )
            }
        }
        val detailInAppParam = listInAppProductQuery.takeIf { it.isNotEmpty() }?.let {
            QueryProductDetailsParams.newBuilder().setProductList(it).build()
        }
        val detailSubsParam = listSubsProductQuery.takeIf { it.isNotEmpty() }?.let {
            QueryProductDetailsParams.newBuilder().setProductList(it).build()
        }
        val deferredInApp = async {
            detailInAppParam?.let { param ->
                queryProductDetails(param)
            } ?: emptyList()
        }
        val deferredSubs = async {
            detailSubsParam?.let { param ->
                queryProductDetails(param)
            } ?: emptyList()
        }
        val results = awaitAll(deferredInApp, deferredSubs)
        results.flatten().also {
            listProductDetail.clear()
            listProductDetail.addAll(it)
        }
        listProductDetail.convertToListModel()
    }

    private fun List<ProductDetails>.convertToListModel(): List<ProductModel> {
        val listProductModel = mutableListOf<ProductModel>()
        for (productDetails in this) {
            val productId = productDetails.productId
            val productName = productDetails.name
            val productTitle = productDetails.title
            val description = productDetails.description
            if (productDetails.productType == ProductType.INAPP && productDetails.oneTimePurchaseOfferDetails != null) {
                val priceCurrencyCode =
                    productDetails.oneTimePurchaseOfferDetails!!.priceCurrencyCode
                val formattedPrice = productDetails.oneTimePurchaseOfferDetails!!.formattedPrice
                val offerTag = productDetails.oneTimePurchaseOfferDetails!!.offerTags

                listProductModel.add(
                    InAppProductModel(
                        productId = productId,
                        productName = productName,
                        productTitle = productTitle,
                        productDescription = description,
                        priceCurrencyCode = priceCurrencyCode,
                        formattedPrice = formattedPrice,
                        isPurchase = false,
                        purchaseTime = 0,
                        offerTag = offerTag
                    )
                )
            } else if (productDetails.productType == ProductType.SUBS && !productDetails.subscriptionOfferDetails.isNullOrEmpty()) {

                val mapBasePlan = HashMap<String, ArrayList<BasePlanModel>>()

                for (subscriptionOfferDetail in productDetails.subscriptionOfferDetails!!) {
                    val basePlanId = subscriptionOfferDetail.basePlanId //String

                    val isBasePlanInList = try {
                        listID.find { it.idProduct == productId }?.listBasePlan?.find { it.basePlanId == basePlanId } != null
                    } catch (e: Exception) {
                        false
                    }

                    if (!isBasePlanInList) continue

                    val offerId = subscriptionOfferDetail.offerId//String

                    val offerTags = subscriptionOfferDetail.offerTags //List<String>
                    val offerToken = subscriptionOfferDetail.offerToken //String

                    //PricingPhases
                    val pricingPhaseList =
                        subscriptionOfferDetail.pricingPhases.pricingPhaseList //List<PricingPhase>
                    val listPricingPhase = mutableListOf<PricingPhaseModel>()
                    for (pricingPhase in pricingPhaseList) {
                        val priceCurrencyCode =
                            pricingPhase.priceCurrencyCode //String
                        val recurrenceMode = pricingPhase.recurrenceMode //Int
                        val priceAmountMicros =
                            pricingPhase.priceAmountMicros //Long
                        val formattedPrice = pricingPhase.formattedPrice // String
                        val billingPeriod = pricingPhase.billingPeriod //String
                        val billingCycleCount = pricingPhase.billingCycleCount //Int
                        listPricingPhase.add(
                            PricingPhaseModel(
                                priceCurrencyCode = priceCurrencyCode,
                                recurrenceMode = recurrenceMode,
                                priceAmountMicros = priceAmountMicros,
                                formattedPrice = formattedPrice,
                                billingPeriod = billingPeriod,
                                billingCycleCount = billingCycleCount
                            )
                        )
                    }

                    var listBasePlanSameId = mapBasePlan[basePlanId]
                    if (listBasePlanSameId == null) {
                        listBasePlanSameId = ArrayList()
                        mapBasePlan[basePlanId] = listBasePlanSameId
                    }

                    listBasePlanSameId.add(
                        BasePlanModel(
                            productId = productId,
                            basePlanId = basePlanId,
                            offerId = offerId,
                            offerTag = offerTags,
                            offerToken = offerToken,
                            listPricingPhase = listPricingPhase
                        )
                    )
                }

                //ưu tiên item có offer id giống với file iap_id nếu không thì ưu tiên item có offer id = null
                val listBasePlan: List<BasePlanModel> = mapBasePlan.values.mapNotNull { basePlans ->
                    when {
                        basePlans.isEmpty() -> null
                        basePlans.size == 1 -> basePlans.first()
                        else -> {
                            basePlans.firstOrNull { basePlanModel ->
                                val basePlanIdModel =
                                    listID.find { it.idProduct == productId }?.listBasePlan?.find { it.basePlanId == basePlanModel.basePlanId }
                                basePlanModel.offerId == basePlanIdModel?.offerId
                            }
                                ?: basePlans.firstOrNull { it.offerId == null }
                                ?: basePlans.first()
                        }
                    }
                }

                listProductModel.add(
                    SubscriptionProductModel(
                        productId = productId,
                        productName = productName,
                        productTitle = productTitle,
                        productDescription = description,
                        listBasePlan = listBasePlan,
                        isPurchase = false,
                        purchaseTime = 0
                    )
                )
            }
        }
        return listProductModel
    }

    private suspend fun queryProductDetails(
        param: QueryProductDetailsParams
    ): List<ProductDetails> {
        val listProductDetails = mutableListOf<ProductDetails>()
        billingClient?.queryProductDetails(param)
            ?.let { productDetailsResult ->
                if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetailsResult.productDetailsList?.let { listProductDetails.addAll(it) }
                }
            }
        return listProductDetails
    }

    private suspend fun getAllPurchase(application: Application): List<Purchase> {
        val isConnected = startConnectionWithTimeout(application)
        if (!isConnected) return emptyList()
        val subsResult = queryPurchasesSuspend(ProductType.SUBS)
        val inAppResult = queryPurchasesSuspend(ProductType.INAPP)
        if (subsResult == null || inAppResult == null) return emptyList()
        return subsResult + inAppResult
    }

    /**
     * Thực hiện query danh sách các purchase đang active từ Google Play Billing
     * theo loại product (INAPP hoặc SUBS).
     *
     * @param productType Loại product cần query:
     * - [BillingClient.ProductType.INAPP] cho one-time product
     * - [BillingClient.ProductType.SUBS] cho subscription
     *
     * @return
     * - Danh sách [Purchase] nếu query thành công
     * - `null` nếu BillingClient trả về lỗi hoặc không kết nối được
     *
     * @note
     * - Chỉ trả về các purchase đang active
     * - Subscription đã hết hạn hoặc bị hủy sẽ không xuất hiện trong kết quả
     * - Hàm được chuyển từ callback sang suspend để dễ sử dụng với coroutine
     * - Nên được gọi sau khi BillingClient đã kết nối thành công
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun queryPurchasesSuspend(
        productType: String
    ): List<Purchase>? = suspendCancellableCoroutine { cont ->
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(productType)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                cont.resume(purchases, null)
            } else {
                cont.resume(null, null)
            }
        }
    }

    private fun onPurchaseUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build()) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            listProductModel.find { purchase.products.contains(it.productId) }
                                ?.let { iapProductModel ->
                                    subscribeInterface?.subscribeSuccess(iapProductModel)
                                }
                        }
                    }
                } else {
                    listProductModel.find { purchase.products.contains(it.productId) }
                        ?.let { iapProductModel ->
                            subscribeInterface?.subscribeSuccess(iapProductModel)
                        }
                }
            }
        } else {
            subscribeInterface?.subscribeError(logEventFailed(billingResult.responseCode))
        }
    }

    private fun logEventFailed(code: Int): String {
        return when (code) {
            -3 -> "Service_Timeout"
            -2 -> "Feature_Not_Supported"
            -1 -> "Service_Disconnected"
            1 -> "User_Canceled"
            2 -> "Service_Unavailable"
            3 -> "Billing_Unavailable"
            4 -> "Item_Unavailable"
            5 -> "Developer_Error"
            6 -> "Error"
            7 -> "Item_Already_Owned"
            8 -> "Item_Not_Owned"
            else -> ""
        }
    }

    fun getProduct(productID: String): ProductModel? {
        return listProductModel.find { it.productId == productID }
    }

    fun getBasePlan(productID: String, basePlanId: String): BasePlanModel? {
        return try {
            (listProductModel.find { it.productId == productID } as SubscriptionProductModel).listBasePlan.find { it.basePlanId == basePlanId }
        } catch (e: Exception) {
            null
        }
    }

    fun hasAnyPurchasedProduct(listProductId: List<String>): Boolean {
        return try {
            listProductModel.filter { it.productId in listProductId }.any { it.isPurchase }
        } catch (e: Exception) {
            false
        }
    }

    fun resetIap(activity: Activity) {
        if (isDebug) {
            CoroutineScope(Dispatchers.IO).launch {
                //xu ly in app
                billingClient?.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(ProductType.INAPP)
                        .build()
                )?.let { purchasesResult ->
                    if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        purchasesResult.purchasesList.forEach {
                            val consumeParams =
                                ConsumeParams.newBuilder()
                                    .setPurchaseToken(it.purchaseToken)
                                    .build()
                            val consumeResult = billingClient?.consumePurchase(consumeParams)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(activity, "Reset IAP InApps Finish", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }
        }
    }

    fun buyIap(activity: Activity, productId: String, basePlanId: String? = null) {
        listProductDetail.find { it.productId == productId }?.let { productDetails ->
            //in app
            if (productDetails.productType == ProductType.INAPP) {
                val billingFlowParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                val productDetailsParamsList = listOf(billingFlowParam.build())
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                billingClient?.launchBillingFlow(activity, billingFlowParams)


            } else if (productDetails.productType == ProductType.SUBS && basePlanId != null) {
                //check xem product id nay da duoc purchase chua

                CoroutineScope(Dispatchers.IO).launch {

                    val offerToken =
                        productDetails.subscriptionOfferDetails?.find { it.basePlanId == basePlanId }?.offerToken
                            ?: ""

                    val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()

                    val productDetailsParamsList =
                        listOf(productDetailsParams)

                    var oldPurchaseToken: String? = null
                    val listPurchase = queryPurchasesSuspend(ProductType.SUBS)

                    listPurchase?.forEach { purchase ->
                        if (productId in purchase.products) {
                            oldPurchaseToken = purchase.purchaseToken
                        }
                    }


                    val billingFlowParams = if (oldPurchaseToken != null) {
                        //upgrade
                        val subscriptionUpdateParams =
                            BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                .setOldPurchaseToken(oldPurchaseToken)
                                .setSubscriptionReplacementMode(
                                    BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE
                                )
                                .build()

                        BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .setSubscriptionUpdateParams(subscriptionUpdateParams)
                            .build()
                    } else {
                        //mua moi
                        BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()
                    }

                    withContext(Dispatchers.Main) {
                        currentBasePlanId = basePlanId
                        Log.d("CHECKIAPPRODUCT", "buyIap: $currentBasePlanId")
                        billingClient?.launchBillingFlow(activity, billingFlowParams)
                    }
                }
            }
        }
    }

    /**
     * Convert Google Play BillingPeriod (ISO-8601) to days.
     *
     * Examples:
     *  - P7D  -> 7
     *  - P1M  -> 30
     *  - P3M  -> 90
     *  - P1Y  -> 365
     */
    fun billingPeriodToDays(period: String): Int {
        val regex = Regex("""P(\d+)([DWMY])""")
        val match = regex.matchEntire(period) ?: return 0

        val value = match.groupValues[1].toInt()
        val unit = match.groupValues[2]

        return when (unit) {
            "D" -> value
            "W" -> value * 7
            "M" -> value * 30   // Google Play convention
            "Y" -> value * 365  // Google Play convention
            else -> 0
        }
    }

    fun BasePlanModel.hasFreeTrial(): Boolean {
        //check xem co phase free trial khong
        if (listProductModel.find { it.productId == productId }?.isPurchase == true) return false
        for (pricingPhase in listPricingPhase) {
            if (pricingPhase.priceAmountMicros == 0L //free
                && pricingPhase.recurrenceMode == 2
                && pricingPhase.billingCycleCount >= 1 //keo dai 1 chu ky
                && billingPeriodToDays(pricingPhase.billingPeriod) > 0 //mien phi so ngay > 0
            ) {
                return true
            }
        }
        return false
    }

    fun BasePlanModel.timeFreeTrial(): Int {
        //check xem co phase free trial khong
        for (pricingPhase in listPricingPhase) {
            if (pricingPhase.priceAmountMicros == 0L //free
                && pricingPhase.recurrenceMode == 2
                && pricingPhase.billingCycleCount >= 1 //keo dai 1 chu ky
                && billingPeriodToDays(pricingPhase.billingPeriod) > 0 //mien phi so ngay > 0
            ) {
                return billingPeriodToDays(pricingPhase.billingPeriod)
            }
        }
        return 0
    }
}
