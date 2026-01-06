package pion.datlt.libads.iap.repository.productRepository

import com.android.billingclient.api.ProductDetails
import pion.datlt.libads.iap.model.IapIdModel

/**
 * Interface for ProductRepository to enable dependency injection and testing.
 */
interface ProductRepository {
    suspend fun queryAllProducts(iapIdModels: List<IapIdModel>): List<ProductDetails>
}
