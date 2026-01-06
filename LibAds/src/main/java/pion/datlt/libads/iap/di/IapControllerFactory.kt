package pion.datlt.libads.iap.di

import android.app.Application
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
     * Repositories receive BillingClientManager to always get current BillingClient instance.
     * @param application Application context
     * @param purchasesUpdatedListener Listener for purchase updates
     * @return IapDependencies container with all initialized dependencies
     */
    fun create(
        application: Application,
        purchasesUpdatedListener: PurchasesUpdatedListener,
    ): IapDependencies {
        val billingClientManager = createBillingClientManager(application, purchasesUpdatedListener)

        // Repositories receive BillingClientManager instead of BillingClient
        // This ensures they always use the current BillingClient after reconnection
        val productRepository = createProductRepository(billingClientManager)
        val purchaseRepository = createPurchaseRepository(billingClientManager)

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
     * @return BillingClientManager instance
     */
    private fun createBillingClientManager(
        application: Application,
        listener: PurchasesUpdatedListener,
    ): BillingClientManager = BillingClientManagerImpl(application, listener)

    /**
     * Creates ProductRepository with injected BillingClientManager.
     * @param billingClientManager The billing client manager
     * @return ProductRepository instance
     */
    private fun createProductRepository(billingClientManager: BillingClientManager): ProductRepository =
        ProductRepositoryImpl(billingClientManager)

    /**
     * Creates PurchaseRepository with injected BillingClientManager.
     * @param billingClientManager The billing client manager
     * @return PurchaseRepository instance
     */
    private fun createPurchaseRepository(billingClientManager: BillingClientManager): PurchaseRepository =
        PurchaseRepositoryImpl(billingClientManager)
}
