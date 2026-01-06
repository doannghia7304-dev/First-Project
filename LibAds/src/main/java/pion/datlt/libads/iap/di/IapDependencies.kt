package pion.datlt.libads.iap.di

import pion.datlt.libads.iap.repository.billingRepository.BillingClientManager
import pion.datlt.libads.iap.repository.productRepository.ProductRepository
import pion.datlt.libads.iap.repository.purchaseRepository.PurchaseRepository

/**
 * Container for all IAP dependencies.
 * Used by the factory to pass dependencies to IapController.
 */
data class IapDependencies(
    val billingClientManager: BillingClientManager,
    val productRepository: ProductRepository,
    val purchaseRepository: PurchaseRepository
)
