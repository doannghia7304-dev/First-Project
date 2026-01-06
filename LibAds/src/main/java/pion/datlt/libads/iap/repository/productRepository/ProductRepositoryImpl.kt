package pion.datlt.libads.iap.repository.productRepository

import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pion.datlt.libads.iap.utils.BillingResponseCode
import pion.datlt.libads.iap.model.IapIdModel
import pion.datlt.libads.iap.repository.billingRepository.BillingClientManager

/**
 * Repository for querying product details from Google Play Billing.
 * Uses BillingClientManager to always get the current BillingClient instance.
 */
class ProductRepositoryImpl(
    private val billingClientManager: BillingClientManager,
) : ProductRepository {

    /**
     * Queries all product details (both INAPP and SUBS) based on configured IDs.
     * @param iapIdModels List of configured product IDs
     * @return List of ProductDetails from Google Play, empty if not connected
     */
    override suspend fun queryAllProducts(iapIdModels: List<IapIdModel>): List<ProductDetails> {
        val client = billingClientManager.getBillingClient() ?: return emptyList()

        return coroutineScope {
            val (inAppProducts, subsProducts) = iapIdModels.partition { it.type == ProductType.INAPP }

            val inAppQuery = buildQueryParams(inAppProducts, ProductType.INAPP)
            val subsQuery = buildQueryParams(subsProducts, ProductType.SUBS)

            val deferredInApp =
                async {
                    inAppQuery?.let { queryProductDetails(client, it) } ?: emptyList()
                }
            val deferredSubs =
                async {
                    subsQuery?.let { queryProductDetails(client, it) } ?: emptyList()
                }

            awaitAll(deferredInApp, deferredSubs).flatten()
        }
    }

    private fun buildQueryParams(
        products: List<IapIdModel>,
        productType: String,
    ): QueryProductDetailsParams? {
        if (products.isEmpty()) return null

        val productList =
            products.map { iapIdModel ->
                QueryProductDetailsParams.Product
                    .newBuilder()
                    .setProductId(iapIdModel.idProduct)
                    .setProductType(productType)
                    .build()
            }

        return QueryProductDetailsParams
            .newBuilder()
            .setProductList(productList)
            .build()
    }

    private suspend fun queryProductDetails(
        client: com.android.billingclient.api.BillingClient,
        params: QueryProductDetailsParams,
    ): List<ProductDetails> {
        val result = client.queryProductDetails(params)

        return if (BillingResponseCode.isSuccess(result.billingResult.responseCode)) {
            result.productDetailsList ?: emptyList()
        } else {
            emptyList()
        }
    }
}
