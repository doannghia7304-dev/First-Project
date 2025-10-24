package pion.datlt.libads

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.ProductType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import pion.datlt.libads.iap.SubscribeInterface
import pion.datlt.libads.iap.model.IapIdModel
import pion.datlt.libads.iap.model.ProductModel
import pion.datlt.libads.iap.model.ProductModel.Companion.convertDataToProduct


object IAPConnector {

    private var isDebug: Boolean? = null
    private var pathJson: String? = null

    private var billingClient: BillingClient? = null
    private var purchasesUpdatedListener: PurchasesUpdatedListener? = null

    private val listID = mutableListOf<IapIdModel>()

    private val listProductModel = mutableListOf<ProductModel>()
    private val listProductDetails = mutableListOf<ProductDetails>()

    val stateCheckIap = MutableStateFlow(StateCheckIap.NONE)

    enum class StateCheckIap {
        NONE , LOADING , FAILED , DONE
    }

    private val subscribeInterface = mutableListOf<SubscribeInterface>()



    private var handler : Handler? = null
    private var isTimeOut = false
    private var timeOutRunnable = {
        if (!isTimeOut && stateCheckIap.value == StateCheckIap.LOADING){
            isTimeOut = true
            stateCheckIap.value = StateCheckIap.FAILED
        }
    }

    fun initIap(application: Application, pathJson: String, isDebug: Boolean? = null , timeOut : Long = 7000L) {
        isTimeOut = false
        handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(timeOutRunnable , timeOut)


        stateCheckIap.value = StateCheckIap.LOADING
        IAPConnector.isDebug = isDebug
        IAPConnector.pathJson = pathJson

        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
                //được gọi khi người dùng bấm mua iap
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        if (!purchase.isAcknowledged) {
                            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken)
                            billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build()) { billingResult1 ->
                                if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                                    listProductModel.find { purchase.products.contains(it.productId) }
                                        ?.let { productModel ->
                                            subscribeInterface.forEach {//goi khi bam mua iap sau khi check isAcknowledged = false
                                                it.subscribeSuccess(productModel)
                                            }
                                        }
                                }
                            }
                        } else {
                            listProductModel.find { purchase.products.contains(it.productId) }
                                ?.let { productModel ->
                                    subscribeInterface.forEach {//goi khi bam mua iap sau khi check isAcknowledged = true
                                        it.subscribeSuccess(productModel)
                                    }
                                }
                        }
                    }
                } else {
                    subscribeInterface.forEach { subscribe -> //goi khi mua iap failed responseCode != BillingClient.BillingResponseCode.OK
                        subscribe.subscribeError(logEventFailed(billingResult.responseCode))
                    }
                }
            }

        val pendingPurchasesParams = PendingPurchasesParams
            .newBuilder()
            .enableOneTimeProducts()
            .build()
        billingClient = BillingClient.newBuilder(application)
            .setListener(purchasesUpdatedListener!!)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        //lấy danh sách các id
        listID.clear()
        listID.addAll(IapIdModel.getDataInput(application, pathJson))
        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    getAllInfo()

                }else{
                    if (!isTimeOut){
                        handler?.removeCallbacks(timeOutRunnable)
                        stateCheckIap.value = StateCheckIap.FAILED
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                if (!isTimeOut){
                    handler?.removeCallbacks(timeOutRunnable)
                    stateCheckIap.value = StateCheckIap.FAILED
                }
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun getAllInfo() {

        val listSubsParams = getSubsDetailParams()
        val listInAppParams = getInAppDetailParams()


        //subs
        val listSubsProductModel = mutableListOf<ProductModel>()
        val listSubsProductDetails = mutableListOf<ProductDetails>()

        //in app
        val listInAppsProductModel = mutableListOf<ProductModel>()
        val listInAppsProductDetails = mutableListOf<ProductDetails>()




        runBlocking {
            val jobSubs = async {
                if (listSubsParams != null){
                    billingClient?.queryProductDetails(listSubsParams)?.let { productDetailsResult ->
                        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (productDetailsResult.productDetailsList != null) {
                                listSubsProductDetails.addAll(productDetailsResult.productDetailsList!!)
                                listSubsProductModel.addAll(convertDataToProduct(listSubsProductDetails))
                            }
                        }
                    }
                }
            }


            val jobInApp = async {
                if (listInAppParams != null){
                    billingClient?.queryProductDetails(listInAppParams)?.let { productDetailsResult ->
                        if (productDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (productDetailsResult.productDetailsList != null) {
                                listInAppsProductDetails.addAll(productDetailsResult.productDetailsList!!)
                                listInAppsProductModel.addAll(
                                    convertDataToProduct(
                                        listInAppsProductDetails
                                    )
                                )
                            }
                        }
                    }
                }
            }


            jobSubs.await()
            jobInApp.await()
            listProductModel.clear()
            listProductDetails.clear()
            listProductModel.addAll(listSubsProductModel)
            listProductModel.addAll(listInAppsProductModel)
            listProductDetails.addAll(listSubsProductDetails)
            listProductDetails.addAll(listInAppsProductDetails)
            checkIapPurchase()
        }


    }

    private fun getSubsDetailParams(): QueryProductDetailsParams? {
        if (listID.find { it.type == ProductType.SUBS } == null){
            return null
        }
        val listProductQuery = mutableListOf<QueryProductDetailsParams.Product>()
        for (item in listID) {
            if (item.type == ProductType.SUBS) {
                listProductQuery.add(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(item.idProduct)
                        .setProductType(item.type)
                        .build()
                )
            }
        }
        return QueryProductDetailsParams
            .newBuilder()
            .setProductList(listProductQuery)
            .build()
    }

    private fun getInAppDetailParams(): QueryProductDetailsParams? {
        if (listID.find { it.type == ProductType.INAPP } == null){
            return null
        }

        val listProductQuery = mutableListOf<QueryProductDetailsParams.Product>()
        for (item in listID) {
            if (item.type == ProductType.INAPP) {
                listProductQuery.add(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(item.idProduct)
                        .setProductType(item.type)
                        .build()
                )
            }
        }

        return QueryProductDetailsParams
            .newBuilder()
            .setProductList(listProductQuery)
            .build()
    }

    private fun checkIapPurchase() {
        runBlocking {
            //check subs
            val listPurchase = mutableListOf<Purchase>()
            var isSubsDone = false
            var isInAppsDone = false
            val jobSubs = async {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.SUBS)
                    .build()
                billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
                    isSubsDone = true
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        listPurchase.addAll(purchases)
                    }
                    if (isInAppsDone) {
                        handleListPurchase(listPurchase)
                    }
                }
            }
            val jobInApps = async {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.INAPP)
                    .build()
                billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
                    isInAppsDone = true
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        listPurchase.addAll(purchases)
                    }
                    if (isSubsDone) {
                        handleListPurchase(listPurchase)
                    }
                }
            }
            jobSubs.await()
            jobInApps.await()
        }

    }

    private fun handleListPurchase(listPurchase: List<Purchase>) {
        CoroutineScope(Dispatchers.IO).launch {
            for (purchase in listPurchase) {
                if (!purchase.isAcknowledged) {
                    //purchase.isAcknowledged Cho biết giao dịch mua đã được xác nhận hay chưa.
                    //phòng cho trường hợp vừa mới mua iap xong reset app
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams.build())
                        ?.let { billingResult ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                listProductModel.find { purchase.products.contains(it.productId) }
                                    ?.let {
                                        it.isPurchase = true
                                    }
                            }
                        }
                } else {
                    //cap nhat lai list model
                    listProductModel.find { purchase.products.contains(it.productId) }
                        ?.let {
                            it.isPurchase = true
                        }
                }
            }

            withContext(Dispatchers.Main) {
                //sau khi xu ly xong
                if (!isTimeOut){
                    handler?.removeCallbacks(timeOutRunnable)
                    stateCheckIap.value = StateCheckIap.DONE
                }
            }
        }
    }

    fun checkIapIsPurchaseById(id: String): Boolean {
        //hàm này không đồng bộ với hàm lấy thông tin về product
        //nếu gọi sớm quá rất có thể sẽ bị rỗng
        for (item in listProductModel) {
            if (item.productId == id && item.isPurchase) {
                return true
            }
        }
        return false
    }

    fun getProductById(id: String): ProductModel? {
        //hàm này không đồng bộ với hàm lấy thông tin về product
        //nếu gọi sớm quá rất có thể sẽ bị rỗng
        for (item in listProductModel) {
            if (item.productId == id) {
                return item
            }
        }
        return null
    }

    fun getAllProductModel(): List<ProductModel> {
        //hàm này không đồng bộ với hàm lấy thông tin về product
        //nếu gọi sớm quá rất có thể sẽ bị rỗng
        return listProductModel
    }

    fun addIAPListener(listener: SubscribeInterface) {
        //hàm này để thêm listener khi mua iap
        subscribeInterface.add(listener)
    }

    fun buyIap(activity: Activity, productId: String) {
        listProductDetails.find { it.productId == productId }?.let { productDetails ->
            val billingFlowParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)

            if (productDetails.productType == BillingClient.ProductType.SUBS) {
                billingFlowParam.setOfferToken(
                    productDetails.subscriptionOfferDetails?.get(0)?.offerToken ?: ""
                )
            }
            val productDetailsParamsList =
                listOf(billingFlowParam.build())

            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()

            billingClient?.launchBillingFlow(activity, billingFlowParams)
        }
    }

    fun resetIap(activity: Activity) {
        if (isDebug == true){
            CoroutineScope(Dispatchers.IO).launch {
                //xu ly in app
                billingClient?.queryPurchasesAsync(QueryPurchasesParams.newBuilder()
                    .setProductType(ProductType.INAPP)
                    .build())?.let { purchasesResult ->
                    if (purchasesResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                        purchasesResult.purchasesList.forEach {
                            val consumeParams =
                                ConsumeParams.newBuilder()
                                    .setPurchaseToken(it.purchaseToken)
                                    .build()
                            val consumeResult = billingClient?.consumePurchase(consumeParams)
                        }
                        withContext(Dispatchers.Main){
                            Toast.makeText(activity, "Reset IAP InApps Finish", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

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

}