package pion.datlt.libads.iap.model

data class BasePlanModel(
    val productId: String,
    val basePlanId: String,
    val offerId: String?,
    val offerTag: List<String>?,
    val offerToken: String,
    val listPricingPhase: List<PricingPhaseModel>,
)
