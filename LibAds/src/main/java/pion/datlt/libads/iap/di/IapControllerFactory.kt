package pion.datlt.libads.iap.di

import android.app.Application
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener
import pion.datlt.libads.iap.repository.billingRepository.BillingClientManagerImpl
import pion.datlt.libads.iap.repository.billingRepository.BillingClientManager
import pion.datlt.libads.iap.repository.productRepository.ProductRepository
import pion.datlt.libads.iap.repository.productRepository.ProductRepositoryImpl
import pion.datlt.libads.iap.repository.purchaseRepository.PurchaseRepository
import pion.datlt.libads.iap.repository.purchaseRepository.PurchaseRepositoryImpl

/**
 * Factory for creating and wiring all IAP dependencies.
 * Handles the creation of BillingClientManager, ProductRepository, and PurchaseRepository.
 */
object IapControllerFactory {
    /**
     * Creates all IAP dependencies.
     * @param application Application context
     * @param purchasesUpdatedListener Listener for purchase updates
     * @return IapDependencies container with all initialized dependencies
     */
    fun create(
        application: Application,
        purchasesUpdatedListener: PurchasesUpdatedListener,
    ): IapDependencies {
        val billingClientManager =
            createBillingClientManager(
                application,
                purchasesUpdatedListener,
            )

        // Force BillingClient creation before creating repositories
        val billingClient =
            billingClientManager.client
                ?: throw IllegalStateException("BillingClient was not initialized properly")

        val productRepository = createProductRepository(billingClient)
        val purchaseRepository = createPurchaseRepository(billingClient)

        return IapDependencies(
            billingClientManager = billingClientManager,
            productRepository = productRepository,
            purchaseRepository = purchaseRepository,
        )
    }

    /**
     * Creates BillingClientManager instance.
     * @param application Application context
     * @param listener Listener for purchase updates
     * @return IBillingClientManager instance
     */
    fun createBillingClientManager(
        application: Application,
        listener: PurchasesUpdatedListener,
    ): BillingClientManager = BillingClientManagerImpl(application, listener)

    /**
     * Creates ProductRepository with injected BillingClient.
     * @param billingClient The billing client
     * @return ProductRepository instance
     */
    fun createProductRepository(billingClient: BillingClient): ProductRepository = ProductRepositoryImpl(billingClient)

    /**
     * Creates PurchaseRepository with injected BillingClient.
     * @param billingClient The billing client
     * @return PurchaseRepository instance
     */
    fun createPurchaseRepository(billingClient: BillingClient): PurchaseRepository = PurchaseRepositoryImpl(billingClient)
}
