package pion.datlt.libads.iap.repository.productRepository

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pion.datlt.libads.iap.utils.BillingResponseCode
import pion.datlt.libads.iap.model.IapIdModel

/**
 * Repository for querying product details from Google Play Billing.
 */
class ProductRepositoryImpl(
    private val billingClient: BillingClient,
) : ProductRepository {

    /**
     * Queries all product details (both INAPP and SUBS) based on configured IDs.
     * @param iapIdModels List of configured product IDs
     * @return List of ProductDetails from Google Play
     */
    override suspend fun queryAllProducts(iapIdModels: List<IapIdModel>): List<ProductDetails> =
        coroutineScope {
            val (inAppProducts, subsProducts) = iapIdModels.partition { it.type == ProductType.INAPP }

            val inAppQuery = buildQueryParams(inAppProducts, ProductType.INAPP)
            val subsQuery = buildQueryParams(subsProducts, ProductType.SUBS)

            val deferredInApp =
                async {
                    inAppQuery?.let { queryProductDetails(it) } ?: emptyList()
                }
            val deferredSubs =
                async {
                    subsQuery?.let { queryProductDetails(it) } ?: emptyList()
                }

            awaitAll(deferredInApp, deferredSubs).flatten()
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

    private suspend fun queryProductDetails(params: QueryProductDetailsParams): List<ProductDetails> {
        val result = billingClient.queryProductDetails(params)

        return if (BillingResponseCode.isSuccess(result.billingResult.responseCode)) {
            result.productDetailsList ?: emptyList()
        } else {
            emptyList()
        }
    }
}
